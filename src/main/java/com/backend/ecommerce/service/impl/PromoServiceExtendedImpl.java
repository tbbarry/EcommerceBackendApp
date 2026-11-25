package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.PromoDto;
import com.backend.ecommerce.entity.Promo;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.PromoRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PromoServiceExtendedImpl {

    private final PromoRepository promoRepository;
    private final ProductPromoRepository productPromoRepository;
    private final VariantPromoRepository variantPromoRepository;
    private final CatalogProductPricingIndexService catalogService;

    public PromoDto deactivatePromo(Integer promoId) {
        Promo promo = promoRepository.findById(promoId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo not found"));

        if (!promo.getIsActive()) {
            throw new BusinessException("Promo already inactive", "PROMO_ALREADY_INACTIVE");
        }

        promo.setIsActive(false);
        Promo saved = promoRepository.save(promo);
        log.info("Promo {} deactivated", promo.getCode());

        refreshCatalogForPromo(promoId);
        return toDto(saved);
    }

    public PromoDto activatePromo(Integer promoId) {
        Promo promo = promoRepository.findById(promoId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo not found"));

        if (promo.getIsActive()) {
            throw new BusinessException("Promo already active", "PROMO_ALREADY_ACTIVE");
        }

        promo.setIsActive(true);
        Promo saved = promoRepository.save(promo);
        log.info("Promo {} activated", promo.getCode());

        refreshCatalogForPromo(promoId);
        return toDto(saved);
    }

    private void refreshCatalogForPromo(Integer promoId) {
        Set<Integer> productIds = new HashSet<>();

        productIds.addAll(productPromoRepository.findProductIdsByPromoId(promoId));
        productIds.addAll(variantPromoRepository.findProductIdsByPromoId(promoId));

        if (!productIds.isEmpty()) {
            catalogService.refreshProducts(productIds);
            log.info("Refreshed catalog for {} products", productIds.size());
        }
    }

    private PromoDto toDto(Promo promo) {
        return new PromoDto(promo.getId(), promo.getCode(), promo.getDiscountValue(),
                promo.getStartDate(), promo.getEndDate(), promo.getIsActive(),
                promo.getCreatedAt(), promo.getUpdatedAt());
    }
}
