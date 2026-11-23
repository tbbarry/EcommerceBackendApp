package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ProductCreateRequest;
import com.backend.ecommerce.dto.ProductImageRequest;
import com.backend.ecommerce.dto.VariantFullRequest;
import com.backend.ecommerce.entity.Attribute;
import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.entity.Category;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductImage;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.repository.*;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import com.backend.ecommerce.service.ImageUploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAutomaticFieldsTest {
    @Mock ProductRepository products;
    @Mock CategoryRepository categories;
    @Mock VariantRepository variants;
    @Mock StockRepository stocks;
    @Mock ProductImageRepository images;
    @Mock AttributeValueRepository values;
    @Mock VariantAttributeValueRepository associations;
    @Mock CatalogProductPricingIndexService index;
    @Mock ImageUploadService uploads;
    @InjectMocks ProductServiceImpl service;

    @Test
    void createsUniqueIdentifiersAndImageDescriptionWithoutUserFields() {
        Category category = new Category();
        category.setName("Vêtements");
        Attribute attribute = new Attribute();
        attribute.setName("Couleur");
        AttributeValue value = new AttributeValue();
        value.setAttribute(attribute);
        value.setValue("Rouge");
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Chemise");
        request.setBrand("Maison");
        request.setPrice(BigDecimal.TEN);
        request.setCategoryId(2);
        request.setSlug("ignore");
        VariantFullRequest variant = new VariantFullRequest();
        variant.setPrice(BigDecimal.TEN);
        variant.setQuantity(1);
        request.setVariants(List.of(variant, variant));
        ProductImageRequest image = new ProductImageRequest();
        image.setUrl("data:image/png;base64,eA==");
        image.setVisualAttributeValueId(3);
        image.setMain(true);
        request.setImages(List.of(image));

        when(categories.findById(2)).thenReturn(Optional.of(category));
        when(values.findById(3)).thenReturn(Optional.of(value));
        when(products.save(any())).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(1);
            return product;
        });
        when(variants.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(uploads.storeProductImageData(anyString(), eq(1))).thenReturn("product_1/image.png");

        service.createProductFull(request);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(products).save(productCaptor.capture());
        assertTrue(productCaptor.getValue().getSlug().startsWith("chemise-maison-vetements-"));
        ArgumentCaptor<Variant> variantCaptor = ArgumentCaptor.forClass(Variant.class);
        verify(variants, times(2)).save(variantCaptor.capture());
        assertNotEquals(variantCaptor.getAllValues().get(0).getSku(), variantCaptor.getAllValues().get(1).getSku());
        ArgumentCaptor<ProductImage> imageCaptor = ArgumentCaptor.forClass(ProductImage.class);
        verify(images).save(imageCaptor.capture());
        assertEquals("Chemise - Couleur : Rouge", imageCaptor.getValue().getAlt());
        assertEquals("product_1/image.png", imageCaptor.getValue().getUrl());
        assertTrue(imageCaptor.getValue().isMain());
    }

    @Test
    void mainImageFlagIsReadFromCreationJson() throws Exception {
        ProductImageRequest image = new ObjectMapper().readValue("{\"url\":\"https://example.com/a.png\",\"isMain\":true}", ProductImageRequest.class);
        assertTrue(image.isMain());
    }

    @Test
    void repeatedProductNamesProduceDifferentSlugs() {
        Product product = new Product();
        product.setName("Chemise");
        product.setBrand("Maison");
        assertNotEquals(ProductIdentifiers.slug(product), ProductIdentifiers.slug(product));
        assertEquals("Chemise", ProductIdentifiers.imageAlt(product, null));
    }
}
