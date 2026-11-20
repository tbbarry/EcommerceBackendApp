package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.*;
import org.springframework.data.domain.Page;

/**
 * Service pour gérer les endpoints admin de produits.
 * Fournit des méthodes optimisées pour l'affichage côté admin.
 */
public interface AdminProductService {

    /**
     * Récupère la liste paginée de tous les produits avec infos essentielles.
     * 
     * @param page numéro de page (0-indexed)
     * @param size nombre d'éléments par page
     * @return Page contenant les produits
     */
    Page<ProductAdminListResponse> getAllProductsForAdmin(int page, int size);

    /**
     * Récupère tous les détails d'un produit.
     * 
     * @param id identifiant du produit
     * @return Détails complets du produit
     */
    ProductAdminDetailResponse getProductDetailForAdmin(Integer id);

    // ===== ENDPOINTS D'ÉCRITURE =====

    /**
     * Modifie les informations du produit.
     * @param productId identifiant du produit
     * @param request données à modifier
     * @return le produit modifié
     */
    ProductAdminDetailResponse updateProduct(Integer productId, AdminProductUpdateRequest request);

    /**
     * Ajoute une variante au produit avec son stock.
     * @param productId identifiant du produit
     * @param request données de la variante
     * @return la variante créée
     */
    VariantAdminResponse createVariant(Integer productId, AdminVariantCreateRequest request);

    /**
     * Modifie une variante (sans toucher au stock).
     * @param productId identifiant du produit
     * @param variantId identifiant de la variante
     * @param request données à modifier
     * @return la variante modifiée
     */
    VariantAdminResponse updateVariant(Integer productId, Integer variantId, AdminVariantUpdateRequest request);

    /**
     * Modifie le stock d'une variante.
     * @param productId identifiant du produit
     * @param variantId identifiant de la variante
     * @param request données du stock
     */
    void updateVariantStock(Integer productId, Integer variantId, AdminStockUpdateRequest request);

    /**
     * Supprime une variante.
     * @param productId identifiant du produit
     * @param variantId identifiant de la variante
     */
    void deleteVariant(Integer productId, Integer variantId);

    /**
     * Supprime plusieurs variantes de façon atomique.
     * @param productId identifiant du produit
     * @param request liste des variantes à supprimer
     */
    void bulkDeleteVariants(Integer productId, AdminVariantBulkDeleteRequest request);

    /**
     * Ajoute une image au produit.
     * @param productId identifiant du produit
     * @param request données de l'image
     * @return l'image créée
     */
    ProductImageDto createImage(Integer productId, AdminImageCreateRequest request);

    /**
     * Modifie une image du produit.
     * @param productId identifiant du produit
     * @param imageId identifiant de l'image
     * @param request données à modifier
     * @return l'image modifiée
     */
    ProductImageDto updateImage(Integer productId, Integer imageId, AdminImageUpdateRequest request);

    /**
     * Supprime une image du produit.
     * @param productId identifiant du produit
     * @param imageId identifiant de l'image
     */
    void deleteImage(Integer productId, Integer imageId);

    /**
     * Change le statut d'un produit (archivage).
     * @param productId identifiant du produit
     * @param request données du statut
     * @return le produit modifié
     */
    ProductAdminDetailResponse updateProductStatus(Integer productId, AdminProductStatusRequest request);
}
