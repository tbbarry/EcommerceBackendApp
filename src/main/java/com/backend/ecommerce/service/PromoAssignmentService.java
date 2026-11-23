package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.PromoAssignmentDto;
import com.backend.ecommerce.dto.PromoAssignmentRequest;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductPromo;
import com.backend.ecommerce.entity.Promo;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.entity.VariantPromo;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.PromoRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoAssignmentService {
    private final PromoRepository promos;
    private final ProductRepository products;
    private final VariantRepository variants;
    private final ProductPromoRepository productPromos;
    private final VariantPromoRepository variantPromos;
    private final CatalogProductPricingIndexService catalog;

    @Transactional(readOnly = true)
    public List<PromoAssignmentDto> list(Integer promoId) {
        if (!promos.existsById(promoId)) {
            throw new ResourceNotFoundException("Promotion introuvable.");
        }
        List<PromoAssignmentDto> result = new ArrayList<>();
        productPromos.findAssignmentsByPromoId(promoId).forEach(item ->
                result.add(new PromoAssignmentDto(item.getId(), "PRODUCT", item.getProduct().getId(),
                        item.getProduct().getName(), null, null)));
        variantPromos.findAssignmentsByPromoId(promoId).forEach(item ->
                result.add(new PromoAssignmentDto(item.getId(), "VARIANT", item.getVariant().getProduct().getId(),
                        item.getVariant().getProduct().getName(), item.getVariant().getId(), item.getVariant().getSku())));
        return result;
    }

    public void add(Integer promoId, PromoAssignmentRequest request) {
        Promo promo = promos.findByIdForUpdate(promoId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion introuvable."));
        if ("PRODUCT".equals(request.type())) {
            Product product = products.findById(request.targetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable."));
            if (productPromos.existsByPromoIdAndProductId(promoId, product.getId())) {
                throw new BusinessException("Cette promotion est déjà attribuée à ce produit.", "PROMO_ALREADY_ASSIGNED");
            }
            productPromos.saveAndFlush(ProductPromo.builder().product(product).promo(promo).build());
            catalog.refreshProduct(product.getId());
        } else if ("VARIANT".equals(request.type())) {
            Variant variant = variants.findById(request.targetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante introuvable."));
            if (variantPromos.existsByPromoIdAndVariantId(promoId, variant.getId())) {
                throw new BusinessException("Cette promotion est déjà attribuée à cette variante.", "PROMO_ALREADY_ASSIGNED");
            }
            variantPromos.saveAndFlush(VariantPromo.builder().variant(variant).promo(promo).build());
            catalog.refreshProduct(variant.getProduct().getId());
        } else {
            throw new BusinessException("Type d'attribution invalide.", "PROMO_ASSIGNMENT_TYPE_INVALID");
        }
    }

    public void remove(Integer promoId, String type, Integer assignmentId) {
        promos.findByIdForUpdate(promoId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion introuvable."));
        Integer productId;
        if ("PRODUCT".equals(type)) {
            ProductPromo item = productPromos.findById(assignmentId)
                    .filter(value -> value.getPromo().getId().equals(promoId))
                    .orElseThrow(() -> new ResourceNotFoundException("Attribution introuvable."));
            productId = item.getProduct().getId();
            productPromos.delete(item);
            productPromos.flush();
        } else if ("VARIANT".equals(type)) {
            VariantPromo item = variantPromos.findById(assignmentId)
                    .filter(value -> value.getPromo().getId().equals(promoId))
                    .orElseThrow(() -> new ResourceNotFoundException("Attribution introuvable."));
            productId = item.getVariant().getProduct().getId();
            variantPromos.delete(item);
            variantPromos.flush();
        } else {
            throw new BusinessException("Type d'attribution invalide.", "PROMO_ASSIGNMENT_TYPE_INVALID");
        }
        catalog.refreshProduct(productId);
    }
}
