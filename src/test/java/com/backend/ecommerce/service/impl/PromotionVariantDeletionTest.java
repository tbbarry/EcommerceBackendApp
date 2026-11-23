package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.AdminVariantBulkDeleteRequest;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.Stock;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.repository.AttributeValueRepository;
import com.backend.ecommerce.repository.CategoryRepository;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.repository.VariantAttributeValueRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import com.backend.ecommerce.service.ImageUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionVariantDeletionTest {
    @Mock private ProductRepository products;
    @Mock private StockRepository stock;
    @Mock private ProductImageRepository images;
    @Mock private VariantAttributeValueRepository variantAttributes;
    @Mock private AttributeValueRepository attributes;
    @Mock private CategoryRepository categories;
    @Mock private CatalogProductPricingIndexService catalog;
    @Mock private ImageUploadService uploads;
    @Mock private VariantPromoRepository variantPromos;
    @InjectMocks private AdminProductServiceImpl service;

    private Variant variant;
    private Product product;

    @BeforeEach
    void setUp() {
        variant = Variant.builder().stock(Stock.builder().reservedQuantity(0).build()).build();
        variant.setId(9);
        product = Product.builder().variants(new ArrayList<>(List.of(variant))).build();
        product.setId(1);
        when(products.findById(1)).thenReturn(Optional.of(product));
    }

    @Test
    void removesVariantPromotionsBeforeDeletingVariant() {
        service.deleteVariant(1, 9);
        verify(variantPromos).deleteByVariantIds(List.of(9));
        verify(variantPromos).flush();
        verify(catalog).refreshProduct(1);
    }

    @Test
    void removesPromotionsForBulkDeletion() {
        service.bulkDeleteVariants(1, new AdminVariantBulkDeleteRequest(List.of(9)));
        verify(variantPromos).deleteByVariantIds(List.of(9));
        verify(variantPromos).flush();
        verify(catalog).refreshProduct(1);
    }

    @Test
    void keepsAssignmentsWhenBulkDeletionIsBlocked() {
        variant.getStock().setReservedQuantity(1);
        assertThrows(ResponseStatusException.class,
                () -> service.bulkDeleteVariants(1, new AdminVariantBulkDeleteRequest(List.of(9))));
        verify(variantPromos, never()).deleteByVariantIds(List.of(9));
    }
}
