package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.*;
import com.backend.ecommerce.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur dédié aux endpoints admin pour la gestion des produits.
 * Endpoints protégés pour les administrateurs uniquement.
 */
@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    /**
     * Récupère la liste paginée de tous les produits avec informations essentielles.
     * Utile pour afficher un tableau de bord/grid de gestion des produits.
     * 
     * @param page numéro de page (0-indexed)
     * @param size nombre d'éléments par page
     * @return Page contenant les produits
     */
    @GetMapping
    public ResponseEntity<Page<ProductAdminListResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminProductService.getAllProductsForAdmin(page, size));
    }

    /**
     * Récupère tous les détails d'un produit pour édition/gestion complète.
     * Inclut : variantes, stocks, images, attributs, dates de modification.
     * 
     * @param id identifiant du produit
     * @return Détails complets du produit
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductAdminDetailResponse> getProductDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(adminProductService.getProductDetailForAdmin(id));
    }

    // ===== ENDPOINTS D'ÉCRITURE =====

    /**
     * 1. Modifie les informations du produit.
     */
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductAdminDetailResponse> updateProduct(
            @PathVariable Integer productId,
            @RequestBody AdminProductUpdateRequest request) {
        return ResponseEntity.ok(adminProductService.updateProduct(productId, request));
    }

    /**
     * 2. Ajoute une variante au produit avec son stock.
     */
    @PostMapping("/{productId}/variants")
    public ResponseEntity<VariantAdminResponse> createVariant(
            @PathVariable Integer productId,
            @RequestBody AdminVariantCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminProductService.createVariant(productId, request));
    }

    /**
     * 3. Modifie une variante (sans toucher au stock).
     */
    @PatchMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<VariantAdminResponse> updateVariant(
            @PathVariable Integer productId,
            @PathVariable Integer variantId,
            @RequestBody AdminVariantUpdateRequest request) {
        return ResponseEntity.ok(adminProductService.updateVariant(productId, variantId, request));
    }

    /**
     * 4. Modifie le stock d'une variante.
     */
    @PatchMapping("/{productId}/variants/{variantId}/stock")
    public ResponseEntity<Void> updateVariantStock(
            @PathVariable Integer productId,
            @PathVariable Integer variantId,
            @RequestBody AdminStockUpdateRequest request) {
        adminProductService.updateVariantStock(productId, variantId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 5. Supprime une variante.
     */
    @DeleteMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<Void> deleteVariant(
            @PathVariable Integer productId,
            @PathVariable Integer variantId) {
        adminProductService.deleteVariant(productId, variantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 6. Supprime plusieurs variantes de façon atomique.
     */
    @PostMapping("/{productId}/variants/bulk-delete")
    public ResponseEntity<Void> bulkDeleteVariants(
            @PathVariable Integer productId,
            @RequestBody AdminVariantBulkDeleteRequest request) {
        adminProductService.bulkDeleteVariants(productId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 7. Ajoute une image au produit.
     */
    @PostMapping("/{productId}/images")
    public ResponseEntity<ProductImageDto> createImage(
            @PathVariable Integer productId,
            @ModelAttribute AdminImageCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminProductService.createImage(productId, request));
    }

    /**
     * 8. Modifie une image du produit.
     */
    @PatchMapping("/{productId}/images/{imageId}")
    public ResponseEntity<ProductImageDto> updateImage(
            @PathVariable Integer productId,
            @PathVariable Integer imageId,
            @RequestBody AdminImageUpdateRequest request) {
        return ResponseEntity.ok(adminProductService.updateImage(productId, imageId, request));
    }

    /**
     * 9. Supprime une image du produit.
     */
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Integer productId,
            @PathVariable Integer imageId) {
        adminProductService.deleteImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 10. Change le statut d'un produit (archivage).
     */
    @PatchMapping("/{productId}/status")
    public ResponseEntity<ProductAdminDetailResponse> updateProductStatus(
            @PathVariable Integer productId,
            @RequestBody AdminProductStatusRequest request) {
        return ResponseEntity.ok(adminProductService.updateProductStatus(productId, request));
    }
}
