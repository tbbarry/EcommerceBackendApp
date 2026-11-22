package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CheckoutRequest;
import com.backend.ecommerce.dto.CheckoutSessionData;
import com.backend.ecommerce.dto.CouponDiscountResult;
import com.backend.ecommerce.entity.Cart;
import com.backend.ecommerce.entity.Attribute;
import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.entity.CartItem;
import com.backend.ecommerce.entity.Coupon;
import com.backend.ecommerce.entity.DeliveryAddress;
import com.backend.ecommerce.entity.DeliveryType;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.entity.VariantAttributeValue;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.CartItemRepository;
import com.backend.ecommerce.repository.CartRepository;
import com.backend.ecommerce.repository.DeliveryAddressRepository;
import com.backend.ecommerce.repository.DeliveryPreferenceRepository;
import com.backend.ecommerce.repository.IdempotencyKeyRepository;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.PaymentRepository;
import com.backend.ecommerce.repository.ShippingMethodRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.CouponService;
import com.backend.ecommerce.service.PromotionPricingService;
import com.backend.ecommerce.service.StockService;
import com.backend.ecommerce.service.StripePaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionCheckoutTest {
    @Mock private TaxServiceImpl tax;
    @Mock private CouponService coupons;
    @Mock private PromotionPricingService pricing;
    @Mock private OrderRepository orders;
    @Mock private PaymentRepository payments;
    @Mock private UserRepository users;
    @Mock private DeliveryAddressRepository addresses;
    @Mock private ShippingMethodRepository shippingMethods;
    @Mock private DeliveryPreferenceRepository preferences;
    @Mock private VariantRepository variants;
    @Mock private CartRepository carts;
    @Mock private CartItemRepository cartItems;
    @Mock private StripePaymentService stripe;
    @Mock private StockService stock;
    @Mock private IdempotencyKeyRepository keys;
    @Mock private ObjectMapper mapper;
    @InjectMocks private OrderServiceImpl service;

    private User user;
    private ShippingMethod shipping;
    private List<CartItem> items;
    private CheckoutRequest request;

    @BeforeEach
    void setUp() throws Exception {
        user = User.builder().firstname("Client").lastname("Test").build();
        Product product = Product.builder().name("Article").description("Description").build();
        Variant variant = Variant.builder().product(product).price(new BigDecimal("50")).sku("SKU").build();
        variant.setAttributeValues(List.of(VariantAttributeValue.builder().attributeValue(
                AttributeValue.builder().attribute(Attribute.builder().name("Couleur").build()).value("Bleu").build()).build()));
        variant.setId(9);
        items = List.of(CartItem.builder().variant(variant).quantity(2).build());
        Cart cart = new Cart();
        cart.setId(3);
        shipping = ShippingMethod.builder().name("Livraison").deliveryType(DeliveryType.HOME).build();
        DeliveryAddress address = DeliveryAddress.builder().firstName("Client").lastName("Test").country("FR").build();
        request = new CheckoutRequest();
        request.setAddressId(2);
        request.setShippingMethodId(4);
        request.setCouponCode("CODE");
        when(users.findById(1)).thenReturn(Optional.of(user));
        when(addresses.findByIdAndUserIdAndDeletedFalse(2, 1)).thenReturn(Optional.of(address));
        when(shippingMethods.findByIdAndActiveTrue(4)).thenReturn(Optional.of(shipping));
        when(carts.findByUserId(1)).thenReturn(Optional.of(cart));
        when(cartItems.findByCartId(3)).thenReturn(items);
        when(orders.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(12);
            return order;
        });
        when(stripe.createCheckoutSession(any(Order.class)))
                .thenReturn(new CheckoutSessionData("https://example.test/pay", "intent", "session"));
    }

    @Test
    void snapshotsPromotionalUnitPricesAndDoesNotConsumeLosingCoupon() throws Exception {
        CouponDiscountResult discount = CouponDiscountResult.builder()
                .discountOnSubtotal(BigDecimal.ZERO).discountOnShipping(BigDecimal.ZERO).totalDiscount(BigDecimal.ZERO).build();
        when(pricing.quote(items, user, "CODE", shipping)).thenReturn(new PromotionPricingService.Quote(
                Map.of(9, new BigDecimal("40.00")), new BigDecimal("80.00"), BigDecimal.ZERO,
                BigDecimal.ZERO, new BigDecimal("0.20"), new BigDecimal("16.00"), new BigDecimal("96.00"),
                null, discount));

        service.checkout(1, request, "test-key");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orders).save(captor.capture());
        Order order = captor.getValue();
        assertEquals(new BigDecimal("40.00"), order.getOrderItems().get(0).getUnitPrice());
        assertEquals(new BigDecimal("80.00"), order.getOrderItems().get(0).getTotalPrice());
        assertEquals("Bleu", order.getOrderItems().get(0).getColorSnapshot());
        assertNull(order.getOrderItems().get(0).getSizeSnapshot());
        assertEquals(new BigDecimal("96.00"), order.getTotal());
        assertNull(order.getCouponCodeUsed());
        verify(coupons, never()).registerUsage(any(), any(), any());
        verify(stripe).createCheckoutSession(eq(order));
    }

    @Test
    void recordsWinningCouponAndUsesItsBaseUnitPrices() {
        items.get(0).getVariant().setAttributeValues(List.of());
        Coupon coupon = Coupon.builder().code("CODE").build();
        CouponDiscountResult discount = CouponDiscountResult.builder()
                .discountOnSubtotal(new BigDecimal("30")).discountOnShipping(BigDecimal.ZERO)
                .totalDiscount(new BigDecimal("30")).build();
        when(pricing.quote(items, user, "CODE", shipping)).thenReturn(new PromotionPricingService.Quote(
                Map.of(9, new BigDecimal("50.00")), new BigDecimal("100.00"), BigDecimal.ZERO,
                BigDecimal.ZERO, new BigDecimal("0.20"), new BigDecimal("14.00"), new BigDecimal("84.00"),
                coupon, discount));

        service.checkout(1, request, "test-key");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orders).save(captor.capture());
        Order order = captor.getValue();
        assertEquals(new BigDecimal("50.00"), order.getOrderItems().get(0).getUnitPrice());
        assertEquals(new BigDecimal("84.00"), order.getTotal());
        assertEquals("CODE", order.getCouponCodeUsed());
        assertEquals("", order.getOrderItems().get(0).getColorSnapshot());
        verify(coupons).registerUsage(coupon, user, order);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void rejectsPickupMethodsBeforeCreatingPayment() throws Exception {
        for (DeliveryType type : List.of(DeliveryType.PICKUP_POINT, DeliveryType.LOCKER)) {
            shipping.setDeliveryType(type);
            assertThrows(BusinessException.class, () -> service.checkout(1, request, "test-key"));
        }
        verify(orders, never()).save(any(Order.class));
        verify(stripe, never()).createCheckoutSession(any(Order.class));
    }
}
