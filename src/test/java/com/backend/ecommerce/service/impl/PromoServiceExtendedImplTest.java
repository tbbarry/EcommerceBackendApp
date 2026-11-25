package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.PromoDto;
import com.backend.ecommerce.entity.Promo;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.PromoRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromoServiceExtendedImplTest {
    @Test
    void activationRefreshesAffectedProductsAndReturnsPromoFields() {
        PromoRepository promos = mock(PromoRepository.class);
        ProductPromoRepository products = mock(ProductPromoRepository.class);
        VariantPromoRepository variants = mock(VariantPromoRepository.class);
        CatalogProductPricingIndexService catalog = mock(CatalogProductPricingIndexService.class);
        Promo promo = Promo.builder().code("SALE").discountValue(new BigDecimal("10"))
                .startDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .endDate(LocalDateTime.of(2027, 1, 1, 0, 0)).isActive(false).build();
        promo.setId(1);
        when(promos.findById(1)).thenReturn(Optional.of(promo));
        when(promos.save(promo)).thenReturn(promo);
        when(products.findProductIdsByPromoId(1)).thenReturn(List.of(10, 11));
        when(variants.findProductIdsByPromoId(1)).thenReturn(List.of(11, 12));
        PromoServiceExtendedImpl service = new PromoServiceExtendedImpl(promos, products, variants, catalog);

        PromoDto result = service.activatePromo(1);

        assertTrue(result.getIsActive());
        assertEquals("SALE", result.getCode());
        assertEquals(promo.getDiscountValue(), result.getDiscountValue());
        verify(catalog).refreshProducts(Set.of(10, 11, 12));
    }
}
