📋 PROMPT BACKEND - ENDPOINTS D'ÉCRITURE ADMIN PRODUITS
======================================================

Implémentation des 10 endpoints d'écriture pour la gestion des produits administrateur.

Le frontend Angular dispose déjà d'une interface complète pour gérer les produits, les variantes, les images et les stocks. Vous devez implémenter les endpoints de modification ci-dessous en respectant strictement le contrat fourni.

---

## 🔐 SÉCURITÉ GLOBALE

Tous les endpoints `/api/admin/products/**` doivent:
✅ Exiger un JWT valide avec le rôle ADMIN
✅ Retourner 401 si l'utilisateur n'est pas authentifié
✅ Retourner 403 s'il n'est pas administrateur
✅ Appliquer les contrôles côté serveur (ne pas compter sur Angular)

```java
@PreAuthorize("hasRole('ADMIN')")
```

---

## 🔄 CYCLE DE VIE DES OPÉRATIONS

Après chaque écriture réussie, le frontend effectue:
```
GET /api/admin/products/{productId}
```

Le détail retourné doit TOUJOURS contenir:
- `variants`: tableau (vide si aucune variante)
- `images`: tableau (vide si aucune image)
- Champ `main` booléen (pas `isMain`) pour les images
- Tous les détails actualisés

---

## 📌 ENDPOINT 1: Modifier les informations du produit

```http
PATCH /api/admin/products/{productId}
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Requête:**
```json
{
  "name": "T-shirt coton premium",
  "description": "Un t-shirt 100% coton",
  "brand": "Nike",
  "categoryId": 3,
  "price": 29.99,
  "slug": "t-shirt-coton-premium"
}
```

**Logique:**
- Modifier uniquement les champs présents
- Valider:
  - `name`: non-vide, longueur max 255 chars
  - `price`: positif, format BigDecimal avec 2 décimales
  - `slug`: format valide (alphanumériques, tirets, minuscules)
  - `categoryId`: vérifier que la catégorie existe
  - `brand`: non-vide si fourni
- Mettre à jour les dates `createdAt` et `updatedAt`
- Appeler `catalogProductPricingIndexService.refreshProduct(productId)`

**Réponse:**
- `200 OK` avec le produit modifié (ou `204 No Content`)

**Erreurs:**
- `400`: validation échouée
- `404`: produit inexistant
- `409`: catégorie inexistante

---

## 📌 ENDPOINT 2: Ajouter une variante

```http
POST /api/admin/products/{productId}/variants
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Requête:**
```json
{
  "sku": "TSHIRT-BLACK-M",
  "price": 29.99,
  "attributeValueIds": [12, 35],
  "quantity": 50
}
```

**Logique:**
- Vérifier que le produit existe
- Valider:
  - `sku`: non-vide, unique au sein du produit (ou globalement selon vos règles)
  - `price`: positif
  - `attributeValueIds`: tableau non-vide, vérifier que toutes les valeurs existent
  - `quantity`: entier >= 0
- Créer l'entité `Variant` avec le SKU et le prix
- Associer chaque valeur d'attribut à la variante via `VariantAttributeValue`
- Créer l'entité `Stock` avec la quantité initiale
  - `quantity = quantity`
  - `reservedQuantity = 0`
  - `availableQuantity = quantity`
- Tout dans une même transaction
- Appeler `catalogProductPricingIndexService.refreshProduct(productId)`

**Réponse:**
- `201 Created` avec la variante créée (y compris le `stock` et les `attributeValues`)

**Erreurs:**
- `400`: validation échouée
- `404`: produit inexistant
- `409`: SKU déjà existant

---

## 📌 ENDPOINT 3: Modifier une variante

```http
PATCH /api/admin/products/{productId}/variants/{variantId}
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Requête:**
```json
{
  "sku": "TSHIRT-BLACK-M",
  "price": 27.99,
  "attributeValueIds": [12, 35]
}
```

**Logique:**
- Vérifier que la variante appartient au produit
- Modifier uniquement les champs présents
- Valider:
  - `sku`: unique au sein du produit (si modifié)
  - `price`: positif
  - `attributeValueIds`: vérifier l'existence des valeurs
- Remplacer les associations d'attributs (supprimer les anciennes, ajouter les nouvelles)
- **Ne pas modifier le stock**
- Appeler `catalogProductPricingIndexService.refreshProduct(productId)`

**Réponse:**
- `200 OK` ou `204 No Content`

**Erreurs:**
- `400`: validation échouée
- `404`: variante ou produit inexistant
- `409`: variante n'appartient pas au produit

---

## 📌 ENDPOINT 4: Modifier le stock d'une variante

```http
PATCH /api/admin/products/{productId}/variants/{variantId}/stock
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Requête:**
```json
{
  "quantity": 85
}
```

**Logique:**
- Vérifier que la variante appartient au produit
- Valider:
  - `quantity`: entier >= 0
  - `quantity >= reservedQuantity` (refuser une réduction sous les réservations)
- Recalculer:
  - `availableQuantity = quantity - reservedQuantity`
- Mettre à jour les dates
- **Atomicité:** protéger contre les modifications concurrentes (utiliser des verrous optimistes ou pessimistes)
- Appeler `catalogProductPricingIndexService.refreshProduct(productId)`

**Réponse:**
- `200 OK` ou `204 No Content`

**Erreurs:**
- `400`: quantité invalide ou inférieure aux réservations
- `404`: variante ou produit inexistant
- `409`: variante n'appartient pas au produit

---

## 📌 ENDPOINT 5: Supprimer une variante

```http
DELETE /api/admin/products/{productId}/variants/{variantId}
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Logique:**
- Vérifier que la variante appartient au produit
- Vérifier les contraintes métier:
  - Si des unités sont réservées, refuser la suppression
  - Vérifier s'il y a des références dans les commandes
- Supprimer la variante et ses associations (stock, attributs)
- Appeler `catalogProductPricingIndexService.refreshProduct(productId)`

**Réponse:**
- `204 No Content`

**Erreurs:**
- `404`: variante ou produit inexistant
- `409`: variante possède des réservations ou des références
  ```json
  {
    "message": "Cette variante possède des unités réservées et ne peut pas être supprimée."
  }
  ```

---

## 📌 ENDPOINT 6: Supprimer plusieurs variantes (Bulk)

```http
POST /api/admin/products/{productId}/variants/bulk-delete
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Requête:**
```json
{
  "variantIds": [101, 102, 103]
}
```

**Logique:**
- **ATOMICITÉ OBLIGATOIRE:** Si une seule variante ne peut pas être supprimée, **AUCUNE** n'est supprimée
- Vérifier que chaque variante:
  - Appartient au produit
  - Peut être supprimée (pas de réservations, pas de références)
- Si une variante pose problème, retourner une erreur 409 avec un message explicite
- Si tout est valide, supprimer toutes les variantes dans une même transaction
- Appeler `catalogProductPricingIndexService.refreshProduct(productId)` une seule fois

**Réponse:**
- `204 No Content`

**Erreurs:**
- `400`: liste vide ou malformée
- `404`: produit inexistant
- `409`: au moins une variante ne peut pas être supprimée
  ```json
  {
    "message": "La variante 102 possède des unités réservées. Aucune suppression n'a été effectuée.",
    "conflictVariantId": 102
  }
  ```

---

## 📌 ENDPOINT 7: Ajouter une image

```http
POST /api/admin/products/{productId}/images
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Requête:**
```json
{
  "url": "https://cdn.example.com/products/tshirt-black.jpg",
  "alt": "T-shirt noir, vue de face",
  "isMain": false,
  "displayOrder": 2,
  "visualAttributeValueId": 12
}
```

**Logique:**
- Vérifier que le produit existe
- Valider:
  - `url`: non-vide, doit commencer par `http://` ou `https://`
  - `alt`: non-vide
  - `isMain`: booléen
  - `displayOrder`: entier >= 0
  - `visualAttributeValueId`: optionnel, vérifier l'existence si fourni
- Créer l'entité `ProductImage`
- **Si `isMain` est true:**
  - Chercher l'éventuelle image principale existante
  - Passer son `main` à false
  - Passer le `main` de la nouvelle image à true
- Appeler `catalogProductPricingIndexService.refreshProduct(productId)`

**Réponse:**
- `201 Created` avec l'image créée (champ `main` booléen, pas `isMain`)

**Erreurs:**
- `400`: validation échouée
- `404`: produit inexistant

---

## 📌 ENDPOINT 8: Modifier une image

```http
PATCH /api/admin/products/{productId}/images/{imageId}
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Requête (exemple 1):**
```json
{
  "alt": "T-shirt noir, vue de face",
  "isMain": true,
  "displayOrder": 1,
  "visualAttributeValueId": null
}
```

**Requête (exemple 2 - seulement modifier l'image principale):**
```json
{
  "isMain": true
}
```

**Logique:**
- Vérifier que l'image appartient au produit
- Modifier uniquement les champs présents
- Valider:
  - `alt`: non-vide si fourni
  - `displayOrder`: >= 0 si fourni
  - `visualAttributeValueId`: vérifier l'existence si fourni
- **Gestion de l'image principale:**
  - Si `isMain: true`:
    - Chercher l'éventuelle image principale existante
    - Passer son `main` à false
    - Passer le `main` de cette image à true
  - Si `visualAttributeValueId: null`:
    - Supprimer l'association (mettre le champ à null)
  - Si `visualAttributeValueId` absent:
    - Ne pas modifier l'association
- Appeler `catalogProductPricingIndexService.refreshProduct(productId)`

**Réponse:**
- `200 OK` ou `204 No Content`

**Erreurs:**
- `400`: validation échouée
- `404`: image ou produit inexistant

---

## 📌 ENDPOINT 9: Supprimer une image

```http
DELETE /api/admin/products/{productId}/images/{imageId}
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Logique:**
- Vérifier que l'image appartient au produit
- Vérifier les contraintes:
  - Si l'image est principale et qu'il existe d'autres images:
    - **Refuser la suppression** avec le message: 
      ```json
      {
        "message": "Impossible de supprimer l'image principale. Choisissez d'abord une autre image comme principale."
      }
      ```
  - Si c'est la seule image:
    - Supprimer (produit sans image est acceptable)
- Supprimer l'image
- Appeler `catalogProductPricingIndexService.refreshProduct(productId)`

**Réponse:**
- `204 No Content`

**Erreurs:**
- `404`: image ou produit inexistant
- `409`: image principale et autres images existent

---

## 📌 ENDPOINT 10: Archiver un produit

```http
PATCH /api/admin/products/{productId}/status
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN_ADMIN}
```

**Requête:**
```json
{
  "status": "ARCHIVED"
}
```

**Logique:**
- Vérifier que le produit existe
- Valider:
  - `status`: doit être "ARCHIVED" (confirmer le statut avec votre modèle)
  - Vérifier que c'est un statut valide dans votre système
- Mettre à jour le statut du produit
- **L'archivage ne doit pas supprimer les données** (conservez tout pour les commandes existantes)
- Mettre à jour les dates
- Appeler `catalogProductPricingIndexService.refreshProduct(productId)`
- **Communiquer si le statut expected ne correspond pas**

**Réponse:**
- `200 OK` ou `204 No Content`

**Erreurs:**
- `400`: statut invalide ou non reconnu
  ```json
  {
    "message": "Statut invalide. Les valeurs acceptées sont: ACTIVE, ARCHIVED."
  }
  ```
- `404`: produit inexistant

---

## 📌 DTOs ATTENDUS

### Request DTOs

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductUpdateRequest {
    private String name;
    private String description;
    private String brand;
    private Integer categoryId;
    private BigDecimal price;
    private String slug;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVariantCreateRequest {
    @NotNull
    private String sku;
    @NotNull
    private BigDecimal price;
    @NotNull
    private List<Integer> attributeValueIds;
    @NotNull
    private Integer quantity;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVariantUpdateRequest {
    private String sku;
    private BigDecimal price;
    private List<Integer> attributeValueIds;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStockUpdateRequest {
    @NotNull
    private Integer quantity;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminImageCreateRequest {
    @NotNull
    private String url;
    @NotNull
    private String alt;
    @NotNull
    private Boolean isMain;
    @NotNull
    private Integer displayOrder;
    private Integer visualAttributeValueId;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminImageUpdateRequest {
    private String alt;
    private Boolean isMain;
    private Integer displayOrder;
    private Integer visualAttributeValueId;  // null supprime l'association
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVariantBulkDeleteRequest {
    @NotEmpty
    private List<Integer> variantIds;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductStatusRequest {
    @NotNull
    private String status;
}
```

---

## 📌 RESPONSE DTOs

Les réponses doivent utiliser les DTOs déjà définis:
- `ProductAdminDetailResponse` (avec `variants[]` et `images[]`)
- Utiliser le champ booléen **`main`** (pas `isMain`) pour les images
- Inclure tous les détails des variantes (stock, attributs)
- Les images vides doivent retourner un tableau vide `[]`

---

## 📌 GESTION DES ERREURS

Format de réponse d'erreur:

```json
{
  "message": "Description lisible du problème",
  "timestamp": "2026-11-20T22:36:49",
  "status": 400
}
```

Codes HTTP attendus:
- `200 OK`: modification réussie avec réponse
- `201 Created`: création réussie
- `204 No Content`: opération réussie sans contenu
- `400 Bad Request`: entrée invalide ou validation échouée
- `401 Unauthorized`: pas d'authentification
- `403 Forbidden`: pas de rôle ADMIN
- `404 Not Found`: ressource inexistante
- `409 Conflict`: conflit métier (SKU existant, réservations, statut invalide, etc.)

---

## 📌 MISE À JOUR DU CATALOGUE

Après chaque opération, appeler:

```java
catalogProductPricingIndexService.refreshProduct(productId);
```

Ce service met à jour:
- L'index de recherche du produit
- Les prix et remises
- Les facettes d'attributs
- Les images

Voir `CatalogProductPricingIndexServiceImpl` pour la logique complète.

---

## 📌 VALIDATIONS MÉTIER

✅ Pas de modifications concurrentes non gérées
✅ Unicité des SKU par produit (ou globalement selon vos règles)
✅ Une seule image principale par produit
✅ Pas de suppression de variantes avec réservations
✅ Atomicité du bulk delete
✅ Les dates createdAt/updatedAt cohérentes
✅ Les calculs de stock corrects (available = total - reserved)

---

## 📌 TESTS UNITAIRES OBLIGATOIRES

```java
@SpringBootTest
public class AdminProductControllerTest {

    @Test
    public void testUpdateProductAsAdmin() {
        // Teste la modification du produit
        // Vérifier la validation et la mise à jour
    }

    @Test
    public void testCreateVariantWithValidAttributes() {
        // Création avec attributs valides
    }

    @Test
    public void testRejectDuplicateSku() {
        // Refuser un SKU déjà existant
    }

    @Test
    public void testRejectStockReductionBelowReserved() {
        // Refuser de réduire le stock sous les réservations
    }

    @Test
    public void testBulkDeleteIsAtomic() {
        // Si une variante pose problème, aucune n'est supprimée
    }

    @Test
    public void testMainImageHandling() {
        // Une seule image principale, remplacée correctement
    }

    @Test
    public void testCannotDeleteMainImageWithOthersPresent() {
        // Refuser la suppression de l'image principale
    }

    @Test
    public void testUnauthorizedAccessFor NonAdmin() {
        // Retourner 403 pour les non-administrateurs
    }

    @Test
    public void testGetDetailAfterSuccessfulWrite() {
        // Vérifier que le GET detail retourne les données modifiées
    }

    @Test
    public void testArchiveProductPreservesData() {
        // L'archivage ne supprime pas les données
    }

    @Test
    public void testCatalogIndexIsRefreshed() {
        // Vérifier que catalogProductPricingIndexService est appelé
    }
}
```

---

## 📋 CHECKLIST D'IMPLÉMENTATION

✅ Endpoints PATCH/POST/DELETE créés dans AdminProductController
✅ Chaque endpoint délégué au service AdminProductService
✅ DTOs de requête créés et validés
✅ Validations métier implémentées
✅ Gestion d'erreurs avec codes HTTP corrects
✅ Transactions pour l'atomicité (bulk delete, création variante + stock)
✅ Vérifications d'appartenance (variante au produit, image au produit)
✅ Mise à jour de l'index catalog après chaque opération
✅ @PreAuthorize("hasRole('ADMIN')") sur chaque endpoint
✅ Tests unitaires pour chaque scénario
✅ Messages d'erreur clairs et lisibles
✅ Les DTOs de réponse utilisent `main` (pas `isMain`) pour les images
✅ Les GET après écriture retournent les données actualisées

---

## 📌 POINTS CLÉS

1. **Frontend dépend du contrat exact**
   → Ne pas inventer de champs supplémentaires
   → Respecter les noms exacts (`isMain` en request, `main` en response)
   → Les tableaux vides sont obligatoires

2. **Sécurité côté serveur**
   → Toujours vérifier l'authentification et le rôle
   → Ne pas compter sur les gardes Angular

3. **Atomicité**
   → Bulk delete: tout ou rien
   → Création variante: variant + stock ensemble
   → Transactions Spring @Transactional

4. **Index catalog**
   → Appeler refreshProduct() après chaque modification
   → Critique pour la recherche et les facettes

5. **Gestion de l'image principale**
   → Une seule par produit
   → Remplacer automatiquement si nécessaire
   → Refuser la suppression si elle est la seule alternative

---

## 📌 ENVIRONNEMENT DE TEST

Endpoints disponibles (front):
```
GET /api/categories
GET /api/attributes/with-values
GET /api/admin/products/{productId}
```

Le frontend utilise `environment.apiBaseUrl` et l'intercepteur JWT existant.

---

VERSION: 1.0
DATE: 2026-11-20
STATUS: Prêt à implémenter

Bonne chance! 🚀
