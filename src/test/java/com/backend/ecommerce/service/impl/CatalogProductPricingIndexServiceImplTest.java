package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.VariantDiscountRow;
import com.backend.ecommerce.dto.ProductCreateRequest;
import com.backend.ecommerce.dto.VariantFullRequest;
import com.backend.ecommerce.entity.Attribute;
import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.entity.CatalogProduct;
import com.backend.ecommerce.entity.CatalogProductFacet;
import com.backend.ecommerce.entity.Category;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductImage;
import com.backend.ecommerce.entity.Promo;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.entity.VariantAttributeValue;
import com.backend.ecommerce.repository.CatalogFacetRepository;
import com.backend.ecommerce.repository.CatalogProductRepository;
import com.backend.ecommerce.repository.CategoryRepository;
import com.backend.ecommerce.repository.AttributeValueRepository;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.PromoRepository;
import com.backend.ecommerce.repository.VariantAttributeValueRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogProductPricingIndexServiceImplTest {

    @Mock private VariantRepository variantRepository;
    @Mock private ProductPromoRepository productPromoRepository;
    @Mock private VariantPromoRepository variantPromoRepository;
    @Mock private CatalogProductRepository catalogProductRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private VariantAttributeValueRepository variantAttributeValueRepository;
    @Mock private CatalogFacetRepository catalogFacetRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private AttributeValueRepository attributeValueRepository;
    @Mock private StockRepository stockRepository;
    @InjectMocks private CatalogProductPricingIndexServiceImpl indexService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder().name("Chemise").slug("chemise").brand("Marque")
                .price(new BigDecimal("100.00")).build();
        product.setId(1);
    }

    @Test
    void createsCatalogAndDistinctFacetsWithBestDiscountPerVariant() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        Attribute attribute = Attribute.builder().name("Couleur").build();
        attribute.setId(3);
        AttributeValue value = AttributeValue.builder().attribute(attribute).value("Rouge").build();
        value.setId(4);
        VariantAttributeValue association = VariantAttributeValue.builder().attributeValue(value).build();
        Variant first = Variant.builder().price(new BigDecimal("100.00")).build();
        first.setId(10);
        Variant second = Variant.builder().price(new BigDecimal("200.00")).build();
        second.setId(11);
        ProductImage image = ProductImage.builder().url("image.jpg").isMain(true).displayOrder(0).build();
        image.setId(20);
        when(productImageRepository.findByProductId(1)).thenReturn(List.of(image));
        when(variantAttributeValueRepository.findDetailedByProductId(1)).thenReturn(List.of(association, association));
        when(variantRepository.findByProductId(1)).thenReturn(List.of(first, second));
        when(productPromoRepository.findActiveGlobalMaxDiscountPercentByProductId(eq(1), any()))
                .thenReturn(new BigDecimal("10"));
        when(variantPromoRepository.findActiveMaxDiscountPercentByProductId(eq(1), any()))
                .thenReturn(List.of(new VariantDiscountRow(11, new BigDecimal("25"))));

        indexService.refreshProduct(1);

        ArgumentCaptor<CatalogProduct> productCaptor = ArgumentCaptor.forClass(CatalogProduct.class);
        verify(catalogProductRepository).save(productCaptor.capture());
        CatalogProduct indexed = productCaptor.getValue();
        assertEquals(1L, indexed.getProductId());
        assertEquals("image.jpg", indexed.getImageUrl());
        assertEquals(new BigDecimal("90.00"), indexed.getFinalPriceMin());
        assertEquals(new BigDecimal("150.00"), indexed.getFinalPriceMax());
        assertEquals("VARIANT", indexed.getPromoScope());
        assertTrue(indexed.getHasDiscount());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<CatalogProductFacet>> facetCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(catalogFacetRepository).saveAll(facetCaptor.capture());
        List<CatalogProductFacet> facets = (List<CatalogProductFacet>) facetCaptor.getValue();
        assertEquals(1, facets.size());
        assertEquals(3L, facets.get(0).getFacetId());
        assertEquals(4L, facets.get(0).getFacetValueId());
        assertEquals("Rouge", facets.get(0).getValueLabel());
        verify(catalogFacetRepository).deleteByProductId(1L);
    }

    @Test
    void appliesGlobalDiscountWithoutVariants() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productPromoRepository.findActiveGlobalMaxDiscountPercentByProductId(eq(1), any()))
                .thenReturn(new BigDecimal("20"));

        indexService.refreshProduct(1);

        ArgumentCaptor<CatalogProduct> captor = ArgumentCaptor.forClass(CatalogProduct.class);
        verify(catalogProductRepository).save(captor.capture());
        assertEquals(new BigDecimal("100.00"), captor.getValue().getBasePriceMin());
        assertEquals(new BigDecimal("80.00"), captor.getValue().getDisplayPrice());
        assertEquals("GLOBAL", captor.getValue().getPromoScope());
    }

    @Test
    void deletesBothSearchTables() {
        indexService.deleteProduct(1);
        verify(catalogFacetRepository).deleteByProductId(1L);
        verify(catalogProductRepository).deleteByProductId(1L);
    }

    @Test
    void deletesPromotionBeforeRefreshingAffectedProducts() {
        PromoRepository promoRepository = org.mockito.Mockito.mock(PromoRepository.class);
        CatalogProductPricingIndexService pricing = org.mockito.Mockito.mock(CatalogProductPricingIndexService.class);
        PromoServiceImpl promoService = new PromoServiceImpl(promoRepository, productPromoRepository,
                variantPromoRepository, pricing);
        Promo promo = new Promo();
        when(promoRepository.findById(7)).thenReturn(Optional.of(promo));
        when(productPromoRepository.findProductIdsByPromoId(7)).thenReturn(List.of(1));
        when(variantPromoRepository.findProductIdsByPromoId(7)).thenReturn(List.of(2));

        promoService.delete(7);

        InOrder order = inOrder(productPromoRepository, variantPromoRepository, promoRepository, pricing);
        order.verify(productPromoRepository).deleteByPromoId(7);
        order.verify(variantPromoRepository).deleteByPromoId(7);
        order.verify(promoRepository).delete(promo);
        order.verify(promoRepository).flush();
        order.verify(pricing).refreshProducts(Set.of(1, 2));
    }

    @Test
    void indexesFullProductAfterSavingVariantAndStock() {
        CatalogProductPricingIndexService pricing = org.mockito.Mockito.mock(CatalogProductPricingIndexService.class);
        ProductServiceImpl productService = new ProductServiceImpl(productRepository, categoryRepository,
                variantRepository, stockRepository, productImageRepository, attributeValueRepository,
                variantAttributeValueRepository, pricing);
        Category category = new Category();
        category.setId(5);
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Chemise");
        request.setBrand("Marque");
        request.setPrice(new BigDecimal("100"));
        request.setCategoryId(5);
        VariantFullRequest variant = new VariantFullRequest();
        variant.setSku("SKU-1");
        variant.setPrice(new BigDecimal("100"));
        variant.setQuantity(5);
        request.setVariants(List.of(variant));
        when(categoryRepository.findById(5)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(1);
            return saved;
        });
        when(variantRepository.save(any(Variant.class))).thenAnswer(invocation -> {
            Variant saved = invocation.getArgument(0);
            saved.setId(10);
            return saved;
        });

        productService.createProductFull(request);

        InOrder order = inOrder(variantRepository, stockRepository, pricing);
        order.verify(variantRepository).save(any(Variant.class));
        order.verify(stockRepository).save(any());
        order.verify(pricing).refreshProduct(1);
    }
}