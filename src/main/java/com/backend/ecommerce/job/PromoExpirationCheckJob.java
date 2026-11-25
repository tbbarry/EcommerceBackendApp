package com.backend.ecommerce.job;

import com.backend.ecommerce.entity.Promo;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.PromoRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromoExpirationCheckJob {

    private final PromoRepository promoRepository;
    private final ProductPromoRepository productPromoRepository;
    private final VariantPromoRepository variantPromoRepository;
    private final CatalogProductPricingIndexService catalogService;

    @Scheduled(cron = "${app.scheduler.promo-expiration-cron:0 */10 * * * *}")
    public void checkAndExpirePromos() {
        try {
            log.info("Starting PromoExpirationCheckJob");

            List<Promo> expiredPromos = promoRepository.findExpiredPromos(LocalDateTime.now());
            Set<Integer> affectedProducts = new HashSet<>();

            for (Promo promo : expiredPromos) {
                promo.setIsActive(false);
                promoRepository.save(promo);
                log.info("Promo {} expired and deactivated", promo.getCode());

                affectedProducts.addAll(productPromoRepository.findProductIdsByPromoId(promo.getId()));
                affectedProducts.addAll(variantPromoRepository.findProductIdsByPromoId(promo.getId()));
            }

            if (!affectedProducts.isEmpty()) {
                catalogService.refreshProducts(affectedProducts);
                log.info("Catalog refreshed for {} products", affectedProducts.size());
            }

            log.info("Job completed: {} promos expired", expiredPromos.size());
        } catch (Exception e) {
            log.error("Job failed: {}", e.getMessage(), e);
        }
    }
}
