package com.backend.ecommerce.security;

import com.backend.ecommerce.controller.AdminProductController;
import com.backend.ecommerce.controller.AdminShippingMethodController;
import com.backend.ecommerce.controller.AdminOrderController;
import com.backend.ecommerce.controller.AdminDashboardController;
import com.backend.ecommerce.controller.AdminOrderMessageController;
import com.backend.ecommerce.controller.StoreSettingsController;
import com.backend.ecommerce.service.StoreSettingsService;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.service.email.OrderMessageTemplateService;
import com.backend.ecommerce.controller.PaymentController;
import com.backend.ecommerce.controller.StripeWebhookController;
import com.backend.ecommerce.controller.ShippingMethodController;
import com.backend.ecommerce.controller.CategoryController;
import com.backend.ecommerce.controller.ProductController;
import com.backend.ecommerce.controller.PromoController;
import com.backend.ecommerce.controller.ReviewController;
import com.backend.ecommerce.service.AdminProductService;
import com.backend.ecommerce.service.CategoryService;
import com.backend.ecommerce.service.ProductDetailService;
import com.backend.ecommerce.service.ProductService;
import com.backend.ecommerce.service.PromoAssignmentService;
import com.backend.ecommerce.service.PromoService;
import com.backend.ecommerce.service.ShippingMethodService;
import com.backend.ecommerce.service.impl.AdminOrderService;
import com.backend.ecommerce.service.impl.StripeReconciliationService;
import com.backend.ecommerce.service.impl.ProductReviewService;
import com.backend.ecommerce.service.PaymentService;
import com.backend.ecommerce.service.SecurityService;
import com.backend.ecommerce.service.StripeWebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AdminProductController.class, AdminShippingMethodController.class, AdminOrderController.class, AdminDashboardController.class, AdminOrderMessageController.class, StoreSettingsController.class, ShippingMethodController.class, CategoryController.class, ProductController.class, PromoController.class, PaymentController.class, StripeWebhookController.class, ReviewController.class})
@Import({SecurityConfig.class, JwtFilter.class})
@TestPropertySource(properties = "stripe.webhook-secret=test-signing-secret")
class SecurityRoutesTest {
    @Autowired MockMvc mvc;
    @MockBean AdminProductService adminProducts;
    @MockBean CategoryService categories;
    @MockBean ProductService products;
    @MockBean ProductDetailService details;
    @MockBean PromoService promos;
    @MockBean PromoAssignmentService assignments;
    @MockBean ShippingMethodService shippingMethods;
    @MockBean AdminOrderService adminOrders;
    @MockBean OrderRepository orderRepository;
    @MockBean StockRepository stockRepository;
    @MockBean OrderMessageTemplateService orderMessages;
    @MockBean StoreSettingsService storeSettings;
    @MockBean PaymentService payments;
    @MockBean StripeReconciliationService reconciliation;
    @MockBean StripeWebhookService webhook;
    @MockBean ProductReviewService reviews;
    @MockBean(name = "securityService") SecurityService securityService;
    @MockBean JwtUtil jwt;
    @MockBean CustomUserDetailsService users;
    @MockBean(name = "corsConfigurationSource") CorsConfigurationSource cors;

    @Test
    void anonymousCanReadCategoriesButCannotAdministerProductsOrPromotions() throws Exception {
        mvc.perform(get("/api/categories")).andExpect(status().isOk());
        mvc.perform(get("/api/store-settings")).andExpect(status().isOk());
        mvc.perform(put("/api/admin/store-settings")).andExpect(status().isUnauthorized());
        mvc.perform(put("/api/admin/store-settings/content")).andExpect(status().isUnauthorized());
        mvc.perform(put("/api/admin/store-settings/logo")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/admin/store-settings/logo")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/shipping-methods")).andExpect(status().isOk());
        mvc.perform(get("/api/admin/shipping-methods")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/orders")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/dashboard")).andExpect(status().isUnauthorized());
        mvc.perform(put("/api/admin/order-messages/PAID")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/products/1/reviews")).andExpect(status().isOk());
        mvc.perform(get("/api/reviews/order-items/1")).andExpect(status().isUnauthorized());
        mvc.perform(put("/api/reviews/order-items/1")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/reviews")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/admin/shipping-methods")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/payments/checkout-session/cs_test/reconcile")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/products")).andExpect(status().is4xxClientError());
        mvc.perform(post("/api/products")).andExpect(status().is4xxClientError());
        mvc.perform(get("/api/promos/admin")).andExpect(status().is4xxClientError());
        mvc.perform(get("/api/promos/1/assignments")).andExpect(status().is4xxClientError());
        mvc.perform(post("/api/test/kafka/payment-completed")).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    void customerCannotUseAdministrationOrModifyTaxonomy() throws Exception {
        mvc.perform(get("/api/admin/products")).andExpect(status().isForbidden());
        mvc.perform(put("/api/admin/store-settings")).andExpect(status().isForbidden());
        mvc.perform(put("/api/admin/store-settings/content")).andExpect(status().isForbidden());
        mvc.perform(put("/api/admin/store-settings/logo")).andExpect(status().isForbidden());
        mvc.perform(delete("/api/admin/store-settings/logo")).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/shipping-methods")).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/orders")).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/dashboard")).andExpect(status().isForbidden());
        mvc.perform(put("/api/admin/order-messages/PAID").contentType(MediaType.APPLICATION_JSON)
                .content("{\"subject\":\"Order\",\"message\":\"Hello\"}")).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/reviews")).andExpect(status().isForbidden());
        mvc.perform(patch("/api/admin/reviews/1/visibility").contentType(MediaType.APPLICATION_JSON)
                .content("{\"visible\":false}")).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/orders/1")).andExpect(status().isForbidden());
        mvc.perform(patch("/api/admin/orders/1/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"expectedStatus\":\"PAID\",\"status\":\"PROCESSING\"}")).andExpect(status().isForbidden());
        mvc.perform(post("/api/admin/shipping-methods")).andExpect(status().isForbidden());
        mvc.perform(post("/api/payments/checkout-session/cs_test/reconcile")).andExpect(status().isForbidden());
        mvc.perform(delete("/api/categories/1")).andExpect(status().isForbidden());
        mvc.perform(get("/api/promos/1/assignments")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void administratorCanUseProtectedRoutes() throws Exception {
        when(adminProducts.getAllProductsForAdmin(anyInt(), anyInt())).thenReturn(Page.empty());
        mvc.perform(get("/api/admin/products")).andExpect(status().isOk());
        mvc.perform(put("/api/admin/store-settings").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"New store\"}")).andExpect(status().isOk());
        mvc.perform(put("/api/admin/store-settings/content").contentType(MediaType.APPLICATION_JSON)
                .content("{\"aboutText\":\"Our story\",\"contactText\":\"Contact us\",\"contactEmail\":\"bad-email\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/api/admin/store-settings/content").contentType(MediaType.APPLICATION_JSON)
                .content("{\"aboutText\":\"Our story\",\"contactText\":\"Contact us\",\"contactEmail\":\"hello@example.com\"}"))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/admin/store-settings/logo")).andExpect(status().isOk());
        mvc.perform(get("/api/admin/shipping-methods")).andExpect(status().isOk());
        when(adminOrders.list("", null, 0, 10)).thenReturn(Page.empty());
        mvc.perform(get("/api/admin/orders")).andExpect(status().isOk());
        when(adminOrders.list("", null, 0, 5)).thenReturn(Page.empty());
        mvc.perform(get("/api/admin/dashboard")).andExpect(status().isOk());
        mvc.perform(get("/api/admin/order-messages")).andExpect(status().isOk());
        mvc.perform(put("/api/admin/order-messages/PAID").contentType(MediaType.APPLICATION_JSON)
                .content("{\"subject\":\"\",\"message\":\"Hello\"}")).andExpect(status().isBadRequest());
        when(reviews.adminReviews(0)).thenReturn(Page.empty());
        mvc.perform(get("/api/admin/reviews")).andExpect(status().isOk());
        mvc.perform(patch("/api/admin/orders/1/status").contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/admin/shipping-methods").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(delete("/api/categories/1")).andExpect(status().isNoContent());
    }

    @Test
    void stripeWebhookAcceptsOnlyAValidSignature() throws Exception {
        String payload = "{\"id\":\"evt_test\",\"object\":\"event\",\"type\":\"checkout.session.completed\",\"data\":{\"object\":{}}}";
        long timestamp = java.time.Instant.now().getEpochSecond();
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(new javax.crypto.spec.SecretKeySpec("test-signing-secret".getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = java.util.HexFormat.of().formatHex(mac.doFinal((timestamp + "." + payload).getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        mvc.perform(post("/api/stripe/webhook").contentType(MediaType.APPLICATION_JSON)
                .header("Stripe-Signature", "t=" + timestamp + ",v1=invalid").content(payload))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/stripe/webhook").contentType(MediaType.APPLICATION_JSON)
                .header("Stripe-Signature", "t=" + timestamp + ",v1=" + signature).content(payload))
                .andExpect(status().isOk());
        verify(webhook).handleCheckoutSessionCompleted(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void customerCanReconcileOnlyTheirOwnSession() throws Exception {
        when(securityService.isCheckoutSessionOwner("cs_owned")).thenReturn(true);
        mvc.perform(post("/api/payments/checkout-session/cs_owned/reconcile")).andExpect(status().isOk());
        mvc.perform(post("/api/payments/checkout-session/cs_other/reconcile")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void reviewRatingMustBeBetweenOneAndFive() throws Exception {
        mvc.perform(put("/api/reviews/order-items/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"rating\":0}")).andExpect(status().isBadRequest());
        mvc.perform(put("/api/reviews/order-items/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"rating\":6}")).andExpect(status().isBadRequest());
        mvc.perform(put("/api/reviews/order-items/1").contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isBadRequest());
    }
}
