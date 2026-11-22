package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.AdminImageCreateRequest;
import com.backend.ecommerce.dto.AdminImageUpdateRequest;
import com.backend.ecommerce.dto.AdminVariantCreateRequest;
import com.backend.ecommerce.entity.Attribute;
import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductImage;
import com.backend.ecommerce.repository.*;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import com.backend.ecommerce.service.ImageUploadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanWrapperImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminProductAutomaticFieldsTest {
    @Mock ProductRepository products;
    @Mock StockRepository stocks;
    @Mock ProductImageRepository images;
    @Mock VariantAttributeValueRepository associations;
    @Mock AttributeValueRepository values;
    @Mock CategoryRepository categories;
    @Mock CatalogProductPricingIndexService index;
    @Mock ImageUploadService uploads;
    @Mock VariantPromoRepository promotions;
    @InjectMocks AdminProductServiceImpl service;

    @Test
    void createsVariantWithoutSkuAndKeepsGeneratedReference() {
        Product product = new Product();
        product.setId(1);
        product.setName("Chemise");
        Attribute attribute = new Attribute();
        attribute.setId(2);
        attribute.setName("Couleur");
        AttributeValue value = new AttributeValue();
        value.setId(3);
        value.setValue("Rouge");
        value.setAttribute(attribute);
        when(products.findById(1)).thenReturn(Optional.of(product));
        when(products.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(values.findById(3)).thenReturn(Optional.of(value));
        AdminVariantCreateRequest request = new AdminVariantCreateRequest(null, BigDecimal.TEN, List.of(3), 2);

        assertTrue(service.createVariant(1, request).getSku().startsWith("CHEMISE-"));
        assertEquals(1, product.getVariants().size());
    }

    @Test
    void createsAndUpdatesGeneratedImageDescription() {
        Product product = new Product();
        product.setId(1);
        product.setName("Chemise");
        Attribute attribute = new Attribute();
        attribute.setName("Couleur");
        AttributeValue value = new AttributeValue();
        value.setId(3);
        value.setValue("Rouge");
        value.setAttribute(attribute);
        when(products.findById(1)).thenReturn(Optional.of(product));
        when(products.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(values.findById(3)).thenReturn(Optional.of(value));
        AdminImageCreateRequest request = new AdminImageCreateRequest();
        request.setUrl("https://example.com/image.jpg");
        request.setVisualAttributeValueId(3);
        new BeanWrapperImpl(request).setPropertyValue("main", true);
        assertTrue(request.isMain());

        assertEquals("Chemise - Couleur : Rouge", service.createImage(1, request).getAlt());
        ProductImage image = product.getImages().get(0);
        image.setId(5);
        AdminImageUpdateRequest update = new AdminImageUpdateRequest();
        update.setVisualAttributeValueId(-1);
        assertEquals("Chemise", service.updateImage(1, 5, update).getAlt());
        assertNull(image.getVisualAttributeValue());
    }
}
