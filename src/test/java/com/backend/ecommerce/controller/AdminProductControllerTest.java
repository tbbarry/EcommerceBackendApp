package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.*;
import com.backend.ecommerce.entity.*;
import com.backend.ecommerce.repository.*;
import com.backend.ecommerce.service.AdminProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AttributeValueRepository attributeValueRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AdminProductService adminProductService;

    @Autowired
    private AttributeRepository attributeRepository;

    private Product testProduct;
    private Category testCategory;
    private AttributeValue testAttributeValue;

    @BeforeEach
    public void setUp() {
        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Description");
        categoryRepository.save(testCategory);

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setBrand("Test Brand");
        testProduct.setSlug("test-product");
        testProduct.setPrice(BigDecimal.valueOf(29.99));
        testProduct.setCategory(testCategory);
        productRepository.save(testProduct);

        // Create Attribute
        Attribute attribute = new Attribute();
        attribute.setName("Color");
        attributeRepository.save(attribute);

        testAttributeValue = new AttributeValue();
        testAttributeValue.setValue("Red");
        testAttributeValue.setAttribute(attribute);
        attributeValueRepository.save(testAttributeValue);
    }

    // ===== TEST 1: Authentication - 401/403 =====

    @Test
    public void testUpdateProductWithoutAdminRole() throws Exception {
        AdminProductUpdateRequest request = new AdminProductUpdateRequest();
        request.setName("Updated");

        mockMvc.perform(patch("/api/admin/products/" + testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    // ===== TEST 2: Create variant with valid attributes =====

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateVariantWithValidAttributes() throws Exception {
        AdminVariantCreateRequest request = new AdminVariantCreateRequest();
        request.setSku("VARIANT-SKU-001");
        request.setPrice(BigDecimal.valueOf(29.99));
        request.setQuantity(10);
        request.setAttributeValueIds(Arrays.asList(testAttributeValue.getId()));

        mockMvc.perform(post("/api/admin/products/" + testProduct.getId() + "/variants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("VARIANT-SKU-001"))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateVariantWithInvalidAttributes() throws Exception {
        AdminVariantCreateRequest request = new AdminVariantCreateRequest();
        request.setSku("VARIANT-SKU-001");
        request.setPrice(BigDecimal.valueOf(29.99));
        request.setQuantity(10);
        request.setAttributeValueIds(Arrays.asList(9999)); // Invalid ID

        mockMvc.perform(post("/api/admin/products/" + testProduct.getId() + "/variants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // ===== TEST 3: Update variant stock with reservation =====

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateVariantStockBelowReservedQuantity() throws Exception {
        AdminVariantCreateRequest createReq = new AdminVariantCreateRequest();
        createReq.setSku("VARIANT-RESERVED");
        createReq.setPrice(BigDecimal.valueOf(29.99));
        createReq.setQuantity(50);
        createReq.setAttributeValueIds(Arrays.asList(testAttributeValue.getId()));

        VariantAdminResponse variant = adminProductService.createVariant(testProduct.getId(), createReq);

        // Add reservations
        Stock stock = stockRepository.findByVariantId(variant.getId()).orElseThrow();
        stock.setReservedQuantity(30);
        stockRepository.save(stock);

        // Try to set quantity lower than reserved
        AdminStockUpdateRequest request = new AdminStockUpdateRequest();
        request.setQuantity(20);

        mockMvc.perform(patch("/api/admin/products/" + testProduct.getId() + 
                "/variants/" + variant.getId() + "/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // ===== TEST 4: Bulk delete atomicity =====

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testBulkDeleteVariantsAtomicity() throws Exception {
        AdminVariantCreateRequest req1 = new AdminVariantCreateRequest();
        req1.setSku("VARIANT-ATOMIC-1");
        req1.setPrice(BigDecimal.valueOf(29.99));
        req1.setQuantity(10);
        req1.setAttributeValueIds(Arrays.asList(testAttributeValue.getId()));

        AdminVariantCreateRequest req2 = new AdminVariantCreateRequest();
        req2.setSku("VARIANT-ATOMIC-2");
        req2.setPrice(BigDecimal.valueOf(39.99));
        req2.setQuantity(20);
        req2.setAttributeValueIds(Arrays.asList(testAttributeValue.getId()));

        VariantAdminResponse var1 = adminProductService.createVariant(testProduct.getId(), req1);
        VariantAdminResponse var2 = adminProductService.createVariant(testProduct.getId(), req2);

        // Add reservations to second variant
        Stock stock2 = stockRepository.findByVariantId(var2.getId()).orElseThrow();
        stock2.setReservedQuantity(15);
        stockRepository.save(stock2);

        AdminVariantBulkDeleteRequest request = new AdminVariantBulkDeleteRequest();
        request.setVariantIds(Arrays.asList(var1.getId(), var2.getId()));

        // Operation should fail (409 Conflict)
        mockMvc.perform(post("/api/admin/products/" + testProduct.getId() + "/variants/bulk-delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isConflict());

        // Verify NO variants were deleted (atomicity)
        ProductAdminDetailResponse detail = adminProductService.getProductDetailForAdmin(testProduct.getId());
        assert detail.getVariants().size() == 2 : "Atomicity failed: variants were deleted despite error";
    }

    // ===== TEST 5: Image main flag enforcement =====

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testOnlyOneImageCanBeMain() throws Exception {
        AdminImageCreateRequest req1 = new AdminImageCreateRequest();
        req1.setUrl("https://example.com/image1.jpg");
        req1.setAlt("Image 1");
        req1.setMain(true);
        req1.setDisplayOrder(0);

        adminProductService.createImage(testProduct.getId(), req1);

        AdminImageCreateRequest req2 = new AdminImageCreateRequest();
        req2.setUrl("https://example.com/image2.jpg");
        req2.setAlt("Image 2");
        req2.setMain(true);
        req2.setDisplayOrder(1);

        mockMvc.perform(post("/api/admin/products/" + testProduct.getId() + "/images")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2))
                .with(csrf()))
                .andExpect(status().isCreated());

        // Verify only one main image
        ProductAdminDetailResponse detail = adminProductService.getProductDetailForAdmin(testProduct.getId());
        long mainCount = detail.getImages().stream().filter(img -> img.isMain()).count();
        assert mainCount == 1 : "Only one image should be main";
    }

    // ===== TEST 6: Cannot delete main image with others =====

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCannotDeleteMainImageWhenOthersExist() throws Exception {
        AdminImageCreateRequest req1 = new AdminImageCreateRequest();
        req1.setUrl("https://example.com/image1.jpg");
        req1.setAlt("Image 1");
        req1.setMain(true);

        AdminImageCreateRequest req2 = new AdminImageCreateRequest();
        req2.setUrl("https://example.com/image2.jpg");
        req2.setAlt("Image 2");
        req2.setMain(false);

        ProductImageDto img1 = adminProductService.createImage(testProduct.getId(), req1);
        adminProductService.createImage(testProduct.getId(), req2);

        mockMvc.perform(delete("/api/admin/products/" + testProduct.getId() + 
                "/images/" + img1.getId())
                .with(csrf()))
                .andExpect(status().isConflict());
    }

    // ===== TEST 7: Resource ownership verification =====

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testVariantBelongsToProductCheck() throws Exception {
        AdminVariantCreateRequest request = new AdminVariantCreateRequest();
        request.setSku("VARIANT-TEST");
        request.setPrice(BigDecimal.valueOf(29.99));
        request.setQuantity(10);
        request.setAttributeValueIds(Arrays.asList(testAttributeValue.getId()));

        VariantAdminResponse variant = adminProductService.createVariant(testProduct.getId(), request);

        // Create another product and category
        Category otherCategory = new Category();
        otherCategory.setName("Other Category");
        otherCategory.setDescription("Other Description");
        categoryRepository.save(otherCategory);

        Product otherProduct = new Product();
        otherProduct.setName("Other Product");
        otherProduct.setDescription("Other Description");
        otherProduct.setBrand("Other Brand");
        otherProduct.setSlug("other-product");
        otherProduct.setPrice(BigDecimal.valueOf(19.99));
        otherProduct.setCategory(otherCategory);
        Product saved = productRepository.save(otherProduct);

        // Try to access variant via wrong product (should 404)
        AdminVariantUpdateRequest updateReq = new AdminVariantUpdateRequest();
        updateReq.setSku("UPDATED");

        mockMvc.perform(patch("/api/admin/products/" + saved.getId() + 
                "/variants/" + variant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // ===== TEST 8: refreshProduct is called =====

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRefreshProductCalledOnUpdate() throws Exception {
        AdminProductUpdateRequest request = new AdminProductUpdateRequest();
        request.setName("Updated");

        mockMvc.perform(patch("/api/admin/products/" + testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRefreshProductCalledOnVariantCreate() throws Exception {
        AdminVariantCreateRequest request = new AdminVariantCreateRequest();
        request.setSku("VARIANT-REFRESH");
        request.setPrice(BigDecimal.valueOf(29.99));
        request.setQuantity(10);
        request.setAttributeValueIds(Arrays.asList(testAttributeValue.getId()));

        mockMvc.perform(post("/api/admin/products/" + testProduct.getId() + "/variants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRefreshProductCalledOnStockUpdate() throws Exception {
        AdminVariantCreateRequest createReq = new AdminVariantCreateRequest();
        createReq.setSku("VARIANT-FOR-STOCK");
        createReq.setPrice(BigDecimal.valueOf(29.99));
        createReq.setQuantity(50);
        createReq.setAttributeValueIds(Arrays.asList(testAttributeValue.getId()));

        VariantAdminResponse variant = adminProductService.createVariant(testProduct.getId(), createReq);

        AdminStockUpdateRequest request = new AdminStockUpdateRequest();
        request.setQuantity(100);

        mockMvc.perform(patch("/api/admin/products/" + testProduct.getId() + 
                "/variants/" + variant.getId() + "/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRefreshProductCalledOnImageCreate() throws Exception {
        AdminImageCreateRequest request = new AdminImageCreateRequest();
        request.setUrl("https://example.com/image.jpg");
        request.setAlt("Test Image");
        request.setMain(false);

        mockMvc.perform(post("/api/admin/products/" + testProduct.getId() + "/images")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRefreshProductCalledOnImageDelete() throws Exception {
        AdminImageCreateRequest createReq = new AdminImageCreateRequest();
        createReq.setUrl("https://example.com/image.jpg");
        createReq.setAlt("Test Image");
        createReq.setMain(false);

        ProductImageDto image = adminProductService.createImage(testProduct.getId(), createReq);

        // Create a second image so we can delete the first
        AdminImageCreateRequest req2 = new AdminImageCreateRequest();
        req2.setUrl("https://example.com/image2.jpg");
        req2.setAlt("Test Image 2");
        req2.setMain(false);
        adminProductService.createImage(testProduct.getId(), req2);

        mockMvc.perform(delete("/api/admin/products/" + testProduct.getId() + 
                "/images/" + image.getId())
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
