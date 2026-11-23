package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.PromoAssignmentRequest;
import com.backend.ecommerce.dto.PromoDto;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductPromo;
import com.backend.ecommerce.entity.Promo;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.PromoRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import com.backend.ecommerce.service.PromoAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromoManagementTest {
    @Mock private PromoRepository promos;
    @Mock private ProductRepository products;
    @Mock private VariantRepository variants;
    @Mock private ProductPromoRepository productPromos;
    @Mock private VariantPromoRepository variantPromos;
    @Mock private CatalogProductPricingIndexService catalog;
    private PromoAssignmentService assignments;
    private PromoServiceImpl promoService;

    @BeforeEach
    void setUp() {
        assignments = new PromoAssignmentService(promos, products, variants, productPromos, variantPromos, catalog);
        promoService = new PromoServiceImpl(promos, productPromos, variantPromos, catalog);
    }

    @Test
    void rejectsInvalidDatesAndDiscounts() {
        PromoDto dto = new PromoDto();
        dto.setCode("SOLDES");
        dto.setIsActive(true);
        dto.setStartDate(LocalDateTime.of(2026, 12, 1, 0, 0));
        dto.setEndDate(LocalDateTime.of(2026, 11, 1, 0, 0));
        dto.setDiscountValue(new BigDecimal("20"));
        assertThrows(BusinessException.class, () -> promoService.create(dto));
        dto.setEndDate(dto.getStartDate().plusDays(1));
        dto.setDiscountValue(new BigDecimal("101"));
        assertThrows(BusinessException.class, () -> promoService.create(dto));
        verify(promos, never()).save(any());
    }

    @Test
    void assignsVariantAndRefreshesItsProduct() {
        Promo promo = new Promo();
        promo.setId(5);
        Product product = Product.builder().build();
        product.setId(7);
        Variant variant = Variant.builder().product(product).build();
        variant.setId(9);
        when(promos.findByIdForUpdate(5)).thenReturn(Optional.of(promo));
        when(variants.findById(9)).thenReturn(Optional.of(variant));

        assignments.add(5, new PromoAssignmentRequest("VARIANT", 9));

        verify(variantPromos).saveAndFlush(any());
        verify(catalog).refreshProduct(7);
    }

    @Test
    void refusesDuplicateProductAssignment() {
        Promo promo = new Promo();
        promo.setId(5);
        Product product = Product.builder().build();
        product.setId(7);
        when(promos.findByIdForUpdate(5)).thenReturn(Optional.of(promo));
        when(products.findById(7)).thenReturn(Optional.of(product));
        when(productPromos.existsByPromoIdAndProductId(5, 7)).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> assignments.add(5, new PromoAssignmentRequest("PRODUCT", 7)));
        verify(productPromos, never()).saveAndFlush(any());
        verify(catalog, never()).refreshProduct(any());
    }

    @Test
    void updatesPromotionAndRefreshesBothProductAndVariantAssignments() {
        Promo promo = new Promo();
        promo.setId(5);
        PromoDto dto = new PromoDto();
        dto.setCode("SOLDES");
        dto.setIsActive(true);
        dto.setStartDate(LocalDateTime.of(2026, 12, 1, 0, 0));
        dto.setEndDate(dto.getStartDate().plusDays(1));
        dto.setDiscountValue(new BigDecimal("25"));
        when(promos.findByIdForUpdate(5)).thenReturn(Optional.of(promo));
        when(promos.save(promo)).thenReturn(promo);
        when(productPromos.findProductIdsByPromoId(5)).thenReturn(List.of(7));
        when(variantPromos.findProductIdsByPromoId(5)).thenReturn(List.of(8));

        promoService.update(5, dto);

        verify(catalog).refreshProducts(Set.of(7, 8));
    }

    @Test
    void removesAssignmentAndRefreshesProduct() {
        Promo promo = new Promo();
        promo.setId(5);
        Product product = Product.builder().build();
        product.setId(7);
        ProductPromo item = ProductPromo.builder().product(product).promo(promo).build();
        when(promos.findByIdForUpdate(5)).thenReturn(Optional.of(promo));
        when(productPromos.findById(3)).thenReturn(Optional.of(item));

        assignments.remove(5, "PRODUCT", 3);

        verify(productPromos).delete(item);
        verify(productPromos).flush();
        verify(catalog).refreshProduct(7);
    }
}
