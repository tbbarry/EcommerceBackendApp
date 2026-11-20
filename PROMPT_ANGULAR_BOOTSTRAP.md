📋 PROMPT ANGULAR - ENDPOINTS ADMIN PRODUITS
=============================================

Passe ce prompt à ton équipe Angular/développeur frontend:

---

## 🎯 TÂCHE: Implémenter l'interface Admin de Gestion des Produits

Vous devez créer une interface d'administration pour gérer les produits avec 2 endpoints disponibles du backend:

### 📌 Endpoints disponibles

**1. Liste paginée des produits (Dashboard)**
```
GET http://localhost:8080/api/admin/products?page=0&size=10
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**2. Détails complets d'un produit (Édition)**
```
GET http://localhost:8080/api/admin/products/{id}
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

### 🎨 IMPORTANT: BOOTSTRAP UNIQUEMENT - PAS DE MATERIAL DESIGN

✅ Utiliser Bootstrap 5+
✅ Classes Bootstrap (btn btn-primary, table, card, pagination, etc.)
✅ Pas de ng-material
✅ Pas de Angular Material
✅ Pas de Material Icons (utiliser Font Awesome ou emoji)

---

## 📦 ÉTAPE 1: Créer les modèles TypeScript

Fichier: `src/app/models/admin-product.model.ts`

```typescript
// Modèle pour la liste paginée
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

// Modèle pour les détails complets
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

// Variante avec stock
export interface VariantAdminResponse {
  id: number;
  sku: string;
  price: number;
  createdAt: string;
  updatedAt: string;
  stock?: VariantStockResponse;
  attributeValues: VariantAttributeValueDto[];
}

// Stock d'une variante
export interface VariantStockResponse {
  id: number;
  quantity: number;
  reservedQuantity: number;
  availableQuantity: number;
  createdAt: string;
  updatedAt: string;
}

// Attribut de variante (couleur, taille, etc.)
export interface VariantAttributeValueDto {
  id: number;
  attributeId: number;
  attributeName: string;
  attributeValueId: number;
  attributeValueName: string;
  createdAt: string;
  updatedAt: string;
}

// Image du produit
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

// Réponse paginée générique
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

## 🔧 ÉTAPE 2: Créer le service Angular

Fichier: `src/app/services/admin-product.service.ts`

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
   */
  getProductsList(page: number = 0, size: number = 10): Observable<PaginatedResponse<ProductAdminListResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PaginatedResponse<ProductAdminListResponse>>(this.apiUrl, { params });
  }

  /**
   * Récupère les détails complets d'un produit
   */
  getProductDetail(id: number): Observable<ProductAdminDetailResponse> {
    return this.http.get<ProductAdminDetailResponse>(`${this.apiUrl}/${id}`);
  }
}
```

---

## 📊 ÉTAPE 3: Composant LISTE (Dashboard avec tableau Bootstrap)

Fichier: `src/app/components/admin/product-list/product-list.component.ts`

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

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProducts();
    }
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadProducts();
    }
  }

  editProduct(id: number): void {
    this.router.navigate(['/admin/products', id]);
  }

  deleteProduct(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      // À implémenter: DELETE /api/admin/products/{id}
      console.log('Suppression du produit', id);
    }
  }

  createProduct(): void {
    this.router.navigate(['/admin/products/new']);
  }

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

Fichier: `src/app/components/admin/product-list/product-list.component.html`

```html
<div class="container-fluid mt-4">
  <!-- Header -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1>Gestion des Produits</h1>
    <button (click)="createProduct()" class="btn btn-primary">
      ➕ Ajouter un produit
    </button>
  </div>

  <!-- Tableau des produits -->
  <div class="table-responsive" *ngIf="!loading">
    <table class="table table-striped table-hover">
      <thead class="table-dark">
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
          <td><small>{{ product.id }}</small></td>
          <td><strong>{{ product.name }}</strong></td>
          <td>{{ product.brand }}</td>
          <td>{{ product.categoryName }}</td>
          <td>{{ product.price | currency }}</td>
          <td>
            <span class="badge bg-info">{{ product.totalVariants }}</span>
          </td>
          <td>
            <span class="badge" 
                  [ngClass]="product.totalStock > 0 ? 'bg-success' : 'bg-danger'">
              {{ product.totalStock }} unités
            </span>
          </td>
          <td>
            <span class="badge bg-primary">{{ product.status }}</span>
          </td>
          <td>
            <button (click)="editProduct(product.id)" 
                    class="btn btn-sm btn-info me-2" 
                    title="Éditer">
              ✏️ Éditer
            </button>
            <button (click)="deleteProduct(product.id)" 
                    class="btn btn-sm btn-danger" 
                    title="Supprimer">
              🗑️ Supprimer
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- Loading spinner Bootstrap -->
  <div class="text-center" *ngIf="loading">
    <div class="spinner-border" role="status">
      <span class="visually-hidden">Chargement...</span>
    </div>
  </div>

  <!-- Pagination Bootstrap -->
  <nav *ngIf="!loading && totalPages > 1" aria-label="Page navigation">
    <ul class="pagination justify-content-center">
      <li class="page-item" [ngClass]="{'disabled': currentPage === 0}">
        <a class="page-link" href="javascript:void(0)" (click)="previousPage()">« Précédent</a>
      </li>
      <li class="page-item" *ngFor="let page of pageNumbers" 
          [ngClass]="{'active': page === currentPage}">
        <a class="page-link" href="javascript:void(0)" (click)="goToPage(page)">
          {{ page + 1 }}
        </a>
      </li>
      <li class="page-item" [ngClass]="{'disabled': currentPage >= totalPages - 1}">
        <a class="page-link" href="javascript:void(0)" (click)="nextPage()">Suivant »</a>
      </li>
    </ul>
  </nav>

  <!-- Info pagination -->
  <div class="text-center text-muted mt-3" *ngIf="!loading">
    <small>
      Page {{ currentPage + 1 }} sur {{ totalPages }} | Total : {{ totalElements }} produits
    </small>
  </div>
</div>
```

---

## 🔍 ÉTAPE 4: Composant DÉTAILS (Bootstrap cards)

Fichier: `src/app/components/admin/product-detail/product-detail.component.ts`

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

  goBack(): void {
    this.router.navigate(['/admin/products']);
  }

  getTotalAvailableStock(): number {
    if (!this.product) return 0;
    return this.product.variants.reduce((total, variant) => {
      return total + (variant.stock?.availableQuantity || 0);
    }, 0);
  }

  getTotalReservedStock(): number {
    if (!this.product) return 0;
    return this.product.variants.reduce((total, variant) => {
      return total + (variant.stock?.reservedQuantity || 0);
    }, 0);
  }
}
```

Fichier: `src/app/components/admin/product-detail/product-detail.component.html`

```html
<div class="container-fluid mt-4" *ngIf="!loading">
  <!-- Header avec boutons -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <button (click)="goBack()" class="btn btn-secondary me-3">← Retour</button>
      <h1 class="d-inline">{{ product?.name }}</h1>
    </div>
    <div>
      <button class="btn btn-primary me-2">✏️ Éditer</button>
      <button class="btn btn-danger">🗑️ Supprimer</button>
    </div>
  </div>

  <!-- Infos générales -->
  <div class="card mb-4">
    <div class="card-header bg-primary text-white">
      <h5 class="mb-0">Informations Générales</h5>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="col-md-6 mb-3">
          <label class="text-muted"><small>Nom</small></label>
          <p><strong>{{ product?.name }}</strong></p>
        </div>
        <div class="col-md-6 mb-3">
          <label class="text-muted"><small>Marque</small></label>
          <p><strong>{{ product?.brand }}</strong></p>
        </div>
        <div class="col-md-6 mb-3">
          <label class="text-muted"><small>Catégorie</small></label>
          <p>{{ product?.categoryName }}</p>
        </div>
        <div class="col-md-6 mb-3">
          <label class="text-muted"><small>Prix</small></label>
          <p><strong class="text-success">{{ product?.price | currency }}</strong></p>
        </div>
        <div class="col-md-6 mb-3">
          <label class="text-muted"><small>Slug</small></label>
          <p><code>{{ product?.slug }}</code></p>
        </div>
        <div class="col-md-6 mb-3">
          <label class="text-muted"><small>Statut</small></label>
          <p>
            <span class="badge bg-success">{{ product?.status }}</span>
          </p>
        </div>
      </div>
      <div>
        <label class="text-muted"><small>Description</small></label>
        <p>{{ product?.description }}</p>
      </div>
    </div>
  </div>

  <!-- Variantes et Stock -->
  <div class="card mb-4">
    <div class="card-header bg-info text-white">
      <h5 class="mb-0">Variantes & Stock</h5>
    </div>
    <div class="card-body">
      <!-- Résumé du stock -->
      <div class="row mb-4">
        <div class="col-md-6">
          <div class="alert alert-success" role="alert">
            <strong>Stock Disponible Total:</strong><br>
            {{ getTotalAvailableStock() }} unités
          </div>
        </div>
        <div class="col-md-6">
          <div class="alert alert-warning" role="alert">
            <strong>Stock Réservé Total:</strong><br>
            {{ getTotalReservedStock() }} unités
          </div>
        </div>
      </div>

      <!-- Tableau des variantes -->
      <div class="table-responsive">
        <table class="table table-sm table-striped">
          <thead class="table-light">
            <tr>
              <th>SKU</th>
              <th>Prix</th>
              <th>Qty</th>
              <th>Réservé</th>
              <th>Disponible</th>
              <th>Attributs</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let variant of product?.variants">
              <td><code>{{ variant.sku }}</code></td>
              <td>{{ variant.price | currency }}</td>
              <td>{{ variant.stock?.quantity }}</td>
              <td>{{ variant.stock?.reservedQuantity }}</td>
              <td>
                <span class="badge" 
                      [ngClass]="variant.stock?.availableQuantity! > 0 ? 'bg-success' : 'bg-danger'">
                  {{ variant.stock?.availableQuantity }}
                </span>
              </td>
              <td>
                <span *ngFor="let attr of variant.attributeValues" 
                      class="badge bg-secondary me-1">
                  {{ attr.attributeName }}: {{ attr.attributeValueName }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- Images -->
  <div class="card mb-4" *ngIf="product?.images && product.images.length > 0">
    <div class="card-header bg-warning text-dark">
      <h5 class="mb-0">Images ({{ product?.images.length }})</h5>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="col-md-4" *ngFor="let image of product?.images">
          <div class="card mb-3">
            <img [src]="image.url" class="card-img-top" [alt]="image.alt" style="height: 200px; object-fit: cover;">
            <div class="card-body">
              <h6 class="card-title">{{ image.alt }}</h6>
              <p class="card-text small">
                <span *ngIf="image.main" class="badge bg-primary">Image principale</span>
                <span class="badge bg-secondary">Ordre: {{ image.displayOrder }}</span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Dates de création/modification -->
  <div class="card bg-light">
    <div class="card-body">
      <div class="row">
        <div class="col-md-6">
          <small class="text-muted">Créé le:</small><br>
          {{ product?.createdAt | date:'dd/MM/yyyy HH:mm' }}
        </div>
        <div class="col-md-6">
          <small class="text-muted">Modifié le:</small><br>
          {{ product?.updatedAt | date:'dd/MM/yyyy HH:mm' }}
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Loading Bootstrap -->
<div class="text-center mt-5" *ngIf="loading">
  <div class="spinner-border" role="status">
    <span class="visually-hidden">Chargement...</span>
  </div>
</div>
```

---

## 🔐 ÉTAPE 5: Interceptor HTTP (Authentification)

Fichier: `src/app/interceptors/auth.interceptor.ts`

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

---

## 🛣️ ÉTAPE 6: Routing

Fichier: `src/app/app-routing.module.ts`

```typescript
const routes: Routes = [
  {
    path: 'admin/products',
    component: ProductListComponent,
    canActivate: [AdminGuard]  // À implémenter
  },
  {
    path: 'admin/products/:id',
    component: ProductDetailComponent,
    canActivate: [AdminGuard]  // À implémenter
  }
];
```

---

## 📦 ÉTAPE 7: Module - Ajouter les imports

Fichier: `src/app/app.module.ts`

```typescript
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { ProductListComponent } from './components/admin/product-list/product-list.component';
import { ProductDetailComponent } from './components/admin/product-detail/product-detail.component';

@NgModule({
  declarations: [
    ProductListComponent,
    ProductDetailComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    // Bootstrap importé dans styles.css
  ],
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

## 🎨 ÉTAPE 8: Importer Bootstrap dans les styles

Fichier: `src/styles.css`

```css
/* Importer Bootstrap */
@import 'bootstrap/dist/css/bootstrap.min.css';

/* Optionnel: Bootstrap Icons ou Font Awesome */
@import 'bootstrap-icons/font/bootstrap-icons.css';
```

Ou dans `angular.json`:
```json
"styles": [
  "node_modules/bootstrap/dist/css/bootstrap.min.css",
  "src/styles.css"
]
```

---

## 📋 CHECKLIST D'IMPLÉMENTATION

✅ Créer les modèles TypeScript (admin-product.model.ts)
✅ Créer le service (admin-product.service.ts)
✅ Créer le composant liste avec Bootstrap
✅ Créer le composant détails avec Bootstrap
✅ Implémenter l'interceptor HTTP
✅ Configurer le routing
✅ Importer Bootstrap
✅ Installer et configurer HttpClientModule
✅ Tester les endpoints avec un token ADMIN

---

## ✅ AVANT DE COMMENCER

- Bootstrap 5+ installé (npm install bootstrap)
- Angular 14+ (compatible avec HttpClient)
- Token JWT ADMIN obtenu (via /api/auth/login)
- Serveur backend en cours d'exécution

---

## 🚀 POINTS CLÉS

✅ **BOOTSTRAP UNIQUEMENT** - Pas de Material Design
✅ Pagination native Bootstrap
✅ Spinner Bootstrap pour loading
✅ Tables Bootstrap pour variantes
✅ Cards Bootstrap pour détails
✅ Badges Bootstrap pour statuts
✅ Buttons Bootstrap standard
✅ Responsive avec Bootstrap grid system

---

Fin du prompt. Bonne implémentation! 🚀
