# 🅰️ Guide d'intégration Angular - Admin Products

Guide complet pour intégrer les endpoints admin produits côté Angular.

## 📋 Table des matières
1. [Service](#service)
2. [Composant Liste](#composant-liste)
3. [Composant Détails](#composant-détails)
4. [Modèles TypeScript](#modèles-typescript)
5. [Interceptor HTTP](#interceptor-http)

---

## 🔧 Service

Créez un service dédié pour communiquer avec l'API admin.

**Fichier : `src/app/services/admin-product.service.ts`**

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductAdminListResponse, ProductAdminDetailResponse, PaginatedResponse } from '../models/admin-product.model';

@Injectable({
  providedIn: 'root'
})
export class AdminProductService {
  private apiUrl = 'http://localhost:8080/api/admin/products';

  constructor(private http: HttpClient) {}

  /**
   * Récupère la liste paginée des produits
   * @param page Numéro de page (0-indexed)
   * @param size Nombre de produits par page
   */
  getProductsList(page: number = 0, size: number = 10): Observable<PaginatedResponse<ProductAdminListResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PaginatedResponse<ProductAdminListResponse>>(this.apiUrl, { params });
  }

  /**
   * Récupère les détails complets d'un produit
   * @param id Identifiant du produit
   */
  getProductDetail(id: number): Observable<ProductAdminDetailResponse> {
    return this.http.get<ProductAdminDetailResponse>(`${this.apiUrl}/${id}`);
  }

  /**
   * Récupère tous les produits sans pagination
   * @param size Nombre maximal de produits à récupérer
   */
  getAllProducts(size: number = 100): Observable<PaginatedResponse<ProductAdminListResponse>> {
    const params = new HttpParams().set('size', size.toString());
    return this.http.get<PaginatedResponse<ProductAdminListResponse>>(this.apiUrl, { params });
  }
}
```

---

## 📊 Composant Liste

Composant pour afficher la liste des produits avec pagination.

**Fichier : `src/app/components/admin/product-list/product-list.component.ts`**

```typescript
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminProductService } from '../../../services/admin-product.service';
import { ProductAdminListResponse } from '../../../models/admin-product.model';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  products: ProductAdminListResponse[] = [];
  loading = false;
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  constructor(
    private adminProductService: AdminProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  /**
   * Charge la liste des produits
   */
  loadProducts(): void {
    this.loading = true;
    this.adminProductService.getProductsList(this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          this.products = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur lors du chargement des produits', error);
          this.loading = false;
        }
      });
  }

  /**
   * Va à la page suivante
   */
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }

  /**
   * Va à la page précédente
   */
  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProducts();
    }
  }

  /**
   * Va à une page spécifique
   */
  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadProducts();
    }
  }

  /**
   * Édite un produit
   */
  editProduct(id: number): void {
    this.router.navigate(['/admin/products', id, 'edit']);
  }

  /**
   * Supprime un produit
   */
  deleteProduct(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      // À implémenter : appel API DELETE /api/admin/products/{id}
      console.log('Suppression du produit', id);
      this.loadProducts();
    }
  }

  /**
   * Crée un nouveau produit
   */
  createProduct(): void {
    this.router.navigate(['/admin/products/new']);
  }

  /**
   * Obtient les pages affichables pour la pagination
   */
  get pageNumbers(): number[] {
    const pages = [];
    const start = Math.max(0, this.currentPage - 2);
    const end = Math.min(this.totalPages, this.currentPage + 3);
    
    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    return pages;
  }
}
```

**Fichier : `src/app/components/admin/product-list/product-list.component.html`**

```html
<div class="product-list-container">
  <div class="header">
    <h1>Gestion des Produits</h1>
    <button (click)="createProduct()" class="btn btn-primary">
      ➕ Ajouter un produit
    </button>
  </div>

  <!-- Tableau des produits -->
  <div class="table-container" *ngIf="!loading">
    <table class="products-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Nom</th>
          <th>Marque</th>
          <th>Catégorie</th>
          <th>Prix</th>
          <th>Variantes</th>
          <th>Stock Total</th>
          <th>Statut</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let product of products">
          <td>{{ product.id }}</td>
          <td><strong>{{ product.name }}</strong></td>
          <td>{{ product.brand }}</td>
          <td>{{ product.categoryName }}</td>
          <td>{{ product.price | currency }}</td>
          <td>{{ product.totalVariants }}</td>
          <td>
            <span class="stock-badge" [ngClass]="{'in-stock': product.totalStock > 0, 'out-of-stock': product.totalStock === 0}">
              {{ product.totalStock }} unités
            </span>
          </td>
          <td>
            <span class="status-badge" [ngClass]="'status-' + product.status.toLowerCase()">
              {{ product.status }}
            </span>
          </td>
          <td class="actions">
            <button (click)="editProduct(product.id)" class="btn btn-sm btn-info" title="Éditer">✏️</button>
            <button (click)="deleteProduct(product.id)" class="btn btn-sm btn-danger" title="Supprimer">🗑️</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- Loading spinner -->
  <div class="spinner" *ngIf="loading">
    <p>Chargement...</p>
  </div>

  <!-- Pagination -->
  <div class="pagination" *ngIf="!loading && totalPages > 1">
    <button (click)="previousPage()" [disabled]="currentPage === 0" class="btn btn-sm">« Précédent</button>
    
    <button *ngFor="let page of pageNumbers" 
            (click)="goToPage(page)"
            [class.active]="page === currentPage"
            class="btn btn-sm pagination-number">
      {{ page + 1 }}
    </button>
    
    <button (click)="nextPage()" [disabled]="currentPage >= totalPages - 1" class="btn btn-sm">Suivant »</button>
  </div>

  <!-- Info pagination -->
  <div class="pagination-info" *ngIf="!loading">
    Page {{ currentPage + 1 }} sur {{ totalPages }} | Total : {{ totalElements }} produits
  </div>
</div>
```

---

## 🔍 Composant Détails

Composant pour afficher les détails complets d'un produit.

**Fichier : `src/app/components/admin/product-detail/product-detail.component.ts`**

```typescript
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminProductService } from '../../../services/admin-product.service';
import { ProductAdminDetailResponse } from '../../../models/admin-product.model';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  product: ProductAdminDetailResponse | null = null;
  loading = false;
  productId: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private adminProductService: AdminProductService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.productId = params['id'];
      if (this.productId) {
        this.loadProduct();
      }
    });
  }

  /**
   * Charge les détails du produit
   */
  loadProduct(): void {
    this.loading = true;
    this.adminProductService.getProductDetail(this.productId)
      .subscribe({
        next: (product) => {
          this.product = product;
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur lors du chargement du produit', error);
          this.loading = false;
        }
      });
  }

  /**
   * Revient à la liste
   */
  goBack(): void {
    this.router.navigate(['/admin/products']);
  }

  /**
   * Édite le produit
   */
  editProduct(): void {
    // À implémenter : mettre le formulaire en mode édition
    console.log('Édition du produit', this.product);
  }

  /**
   * Supprime le produit
   */
  deleteProduct(): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      // À implémenter : appel API DELETE
      console.log('Suppression du produit', this.productId);
      this.goBack();
    }
  }

  /**
   * Obtient la quantité disponible totale
   */
  getTotalAvailableStock(): number {
    if (!this.product) return 0;
    return this.product.variants.reduce((total, variant) => {
      return total + (variant.stock?.availableQuantity || 0);
    }, 0);
  }

  /**
   * Obtient la quantité réservée totale
   */
  getTotalReservedStock(): number {
    if (!this.product) return 0;
    return this.product.variants.reduce((total, variant) => {
      return total + (variant.stock?.reservedQuantity || 0);
    }, 0);
  }
}
```

**Fichier : `src/app/components/admin/product-detail/product-detail.component.html`**

```html
<div class="product-detail-container" *ngIf="!loading">
  <div class="header">
    <button (click)="goBack()" class="btn btn-secondary">← Retour</button>
    <h1>{{ product?.name }}</h1>
    <div class="actions">
      <button (click)="editProduct()" class="btn btn-primary">✏️ Éditer</button>
      <button (click)="deleteProduct()" class="btn btn-danger">🗑️ Supprimer</button>
    </div>
  </div>

  <!-- Infos générales -->
  <section class="product-info">
    <h2>Informations Générales</h2>
    <div class="info-grid">
      <div class="info-item">
        <label>Nom :</label>
        <p>{{ product?.name }}</p>
      </div>
      <div class="info-item">
        <label>Marque :</label>
        <p>{{ product?.brand }}</p>
      </div>
      <div class="info-item">
        <label>Catégorie :</label>
        <p>{{ product?.categoryName }}</p>
      </div>
      <div class="info-item">
        <label>Prix :</label>
        <p><strong>{{ product?.price | currency }}</strong></p>
      </div>
      <div class="info-item">
        <label>Slug :</label>
        <p>{{ product?.slug }}</p>
      </div>
      <div class="info-item">
        <label>Statut :</label>
        <span class="status-badge" [ngClass]="'status-' + product?.status?.toLowerCase()">
          {{ product?.status }}
        </span>
      </div>
    </div>
    <div class="description">
      <label>Description :</label>
      <p>{{ product?.description }}</p>
    </div>
  </section>

  <!-- Variantes et Stock -->
  <section class="variants-section">
    <h2>Variantes & Stock</h2>
    <div class="stock-summary">
      <div class="stock-item">
        <label>Stock Disponible Total :</label>
        <strong class="in-stock">{{ getTotalAvailableStock() }} unités</strong>
      </div>
      <div class="stock-item">
        <label>Stock Réservé Total :</label>
        <strong class="reserved">{{ getTotalReservedStock() }} unités</strong>
      </div>
    </div>

    <table class="variants-table">
      <thead>
        <tr>
          <th>SKU</th>
          <th>Prix</th>
          <th>Quantité</th>
          <th>Réservé</th>
          <th>Disponible</th>
          <th>Attributs</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let variant of product?.variants">
          <td><strong>{{ variant.sku }}</strong></td>
          <td>{{ variant.price | currency }}</td>
          <td>{{ variant.stock?.quantity }}</td>
          <td>{{ variant.stock?.reservedQuantity }}</td>
          <td>
            <span class="stock-badge" [ngClass]="{'in-stock': variant.stock?.availableQuantity! > 0, 'out-of-stock': variant.stock?.availableQuantity === 0}">
              {{ variant.stock?.availableQuantity }}
            </span>
          </td>
          <td>
            <div class="attributes">
              <span *ngFor="let attr of variant.attributeValues" class="attribute-badge">
                {{ attr.attributeName }}: {{ attr.attributeValueName }}
              </span>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </section>

  <!-- Images -->
  <section class="images-section" *ngIf="product?.images && product.images.length > 0">
    <h2>Images</h2>
    <div class="images-grid">
      <div *ngFor="let image of product?.images" class="image-card">
        <img [src]="image.url" [alt]="image.alt" class="product-image">
        <div class="image-info">
          <p><strong>{{ image.alt }}</strong></p>
          <span *ngIf="image.main" class="badge badge-primary">Image principale</span>
          <span class="order">Ordre: {{ image.displayOrder }}</span>
        </div>
      </div>
    </div>
  </section>

  <!-- Dates -->
  <section class="metadata">
    <div class="metadata-item">
      <label>Créé le :</label>
      <p>{{ product?.createdAt | date:'dd/MM/yyyy HH:mm' }}</p>
    </div>
    <div class="metadata-item">
      <label>Modifié le :</label>
      <p>{{ product?.updatedAt | date:'dd/MM/yyyy HH:mm' }}</p>
    </div>
  </section>
</div>

<!-- Loading -->
<div class="spinner" *ngIf="loading">
  <p>Chargement...</p>
</div>
```

---

## 📦 Modèles TypeScript

Créez les interfaces TypeScript correspondant aux modèles backend.

**Fichier : `src/app/models/admin-product.model.ts`**

```typescript
/**
 * Réponse pour la liste paginée des produits (dashboard)
 */
export interface ProductAdminListResponse {
  id: number;
  name: string;
  brand: string;
  categoryName: string;
  price: number;
  totalVariants: number;
  totalStock: number;
  status: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * Réponse pour les détails complets d'un produit
 */
export interface ProductAdminDetailResponse {
  id: number;
  name: string;
  description: string;
  brand: string;
  slug: string;
  price: number;
  categoryId: number;
  categoryName: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  variants: VariantAdminResponse[];
  images: ProductImageDto[];
}

/**
 * Réponse pour une variante avec détails de stock
 */
export interface VariantAdminResponse {
  id: number;
  sku: string;
  price: number;
  createdAt: string;
  updatedAt: string;
  stock?: VariantStockResponse;
  attributeValues: VariantAttributeValueDto[];
}

/**
 * Infos de stock d'une variante
 */
export interface VariantStockResponse {
  id: number;
  quantity: number;
  reservedQuantity: number;
  availableQuantity: number;
  createdAt: string;
  updatedAt: string;
}

/**
 * Attribut d'une variante
 */
export interface VariantAttributeValueDto {
  id: number;
  attributeId: number;
  attributeName: string;
  attributeValueId: number;
  attributeValueName: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * Image d'un produit
 */
export interface ProductImageDto {
  id: number;
  url: string;
  alt: string;
  main: boolean;
  displayOrder: number;
  visualAttributeValueId?: number;
  visualAttributeValueName?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * Réponse paginée générique
 */
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage?: number;
  size: number;
  number: number;
  pageable: any;
  first: boolean;
  last: boolean;
  empty: boolean;
  sort: any;
}
```

---

## 🔐 Interceptor HTTP

Créez un interceptor pour ajouter le token d'authentification automatiquement.

**Fichier : `src/app/interceptors/auth.interceptor.ts`**

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(req);
  }
}
```

**Fichier : `src/app/app.module.ts` (ou app.config.ts pour standalone)**

```typescript
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';

@NgModule({
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
})
export class AppModule { }
```

---

## 🛣️ Routing

Configurez le routing pour accéder aux composants.

**Fichier : `src/app/app-routing.module.ts`**

```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductListComponent } from './components/admin/product-list/product-list.component';
import { ProductDetailComponent } from './components/admin/product-detail/product-detail.component';
import { AdminGuard } from './guards/admin.guard';

const routes: Routes = [
  {
    path: 'admin/products',
    component: ProductListComponent,
    canActivate: [AdminGuard]
  },
  {
    path: 'admin/products/:id/edit',
    component: ProductDetailComponent,
    canActivate: [AdminGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

---

## ✅ Checklist d'implémentation

- [ ] Créer le service `AdminProductService`
- [ ] Créer les interfaces TypeScript
- [ ] Créer l'interceptor HTTP pour l'authentification
- [ ] Créer le composant liste avec pagination
- [ ] Créer le composant détails
- [ ] Configurer le routing
- [ ] Tester avec un token ADMIN valide
- [ ] Implémenter la suppression (DELETE)
- [ ] Implémenter l'édition (PUT/PATCH)
- [ ] Implémenter la création (POST)
- [ ] Ajouter les filtres et tri (optionnel)

---

**Date** : 2026-11-20  
**Version** : 1.0  
**Status** : ✅ Prêt pour implémentation
