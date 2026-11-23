package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.entity.Attribute;
import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.entity.ProductImage;
import com.backend.ecommerce.entity.VariantAttributeValue;
import com.backend.ecommerce.repository.AttributeRepository;
import com.backend.ecommerce.repository.AttributeValueRepository;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.repository.VariantAttributeValueRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mapping.PropertyPath;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttributeDeletionTest {
    @Mock private AttributeRepository attributes;
    @Mock private AttributeValueRepository values;
    @Mock private VariantAttributeValueRepository variants;
    @Mock private ProductImageRepository images;
    @Mock private CatalogProductPricingIndexService catalog;
    private AttributeServiceImpl attributeService;
    private AttributeValueServiceImpl valueService;

    @BeforeEach
    void setUp() {
        attributeService = new AttributeServiceImpl(attributes, variants, images, catalog);
        valueService = new AttributeValueServiceImpl(values, attributes, variants, images, catalog);
    }

    @Test
    void refusesToDeleteAttributeUsedByVariant() {
        when(attributes.findById(1)).thenReturn(Optional.of(new Attribute()));
        when(variants.existsByAttributeValueAttributeId(1)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> attributeService.delete(1));
        verify(attributes, never()).delete(any());
    }

    @Test
    void refusesToDeleteValueUsedByImage() {
        when(values.findById(2)).thenReturn(Optional.of(new AttributeValue()));
        when(images.existsByVisualAttributeValueId(2)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> valueService.delete(2));
        verify(values, never()).delete(any());
    }

    @Test
    void repositoryUsagePathsResolveAcrossRelations() {
        PropertyPath.from("visualAttributeValueAttributeId", ProductImage.class);
        PropertyPath.from("attributeValueAttributeId", VariantAttributeValue.class);
    }
}
