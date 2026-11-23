package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductPromo;
import com.backend.ecommerce.entity.Promo;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.repository.VariantRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductDetailPromotionTest {
    @Test
    void usesProductPriceAndGlobalPromotionWhenThereAreNoVariants() {
        ProductRepository products = mock(ProductRepository.class);
        VariantRepository variants = mock(VariantRepository.class);
        ProductImageRepository images = mock(ProductImageRepository.class);
        ProductPromoRepository productPromos = mock(ProductPromoRepository.class);
        VariantPromoRepository variantPromos = mock(VariantPromoRepository.class);
        Product product = Product.builder().name("Article").slug("article").price(new BigDecimal("100")).build();
        product.setId(1);
        Promo promo = Promo.builder().code("ETE").discountValue(new BigDecimal("20")).build();
        when(products.findBySlug("article")).thenReturn(Optional.of(product));
        when(productPromos.findActiveByProductId(eq(1), any()))
                .thenReturn(List.of(ProductPromo.builder().product(product).promo(promo).build()));

        var result = new ProductDetailServiceImpl(products, variants, images, productPromos, variantPromos)
                .getBySlug("article");

        assertEquals(new BigDecimal("100.00"), result.getPricing().getBasePriceMin());
        assertEquals(new BigDecimal("80.00"), result.getPricing().getFinalPriceMin());
        assertEquals("GLOBAL", result.getPricing().getPromoScope());
    }
}
