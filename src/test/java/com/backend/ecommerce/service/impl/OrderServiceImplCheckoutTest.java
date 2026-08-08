package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CheckoutRequest;
import com.backend.ecommerce.dto.OrderCheckoutResponse;
import com.backend.ecommerce.entity.Cart;
import com.backend.ecommerce.entity.CartItem;
import com.backend.ecommerce.entity.DeliveryAddress;
import com.backend.ecommerce.entity.DeliveryType;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.CartItemRepository;
import com.backend.ecommerce.repository.CartRepository;
import com.backend.ecommerce.repository.DeliveryAddressRepository;
import com.backend.ecommerce.repository.DeliveryPreferenceRepository;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.ShippingMethodRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.ShippingCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplCheckoutTest {

    @Mock
    private TaxServiceImpl taxServiceImpl;
    @Mock
    private ShippingCalculationService shippingCalculationService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DeliveryAddressRepository deliveryAddressRepository;
    @Mock
    private ShippingMethodRepository shippingMethodRepository;
    @Mock
    private DeliveryPreferenceRepository deliveryPreferenceRepository;
    @Mock
    private VariantRepository variantRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private DeliveryAddress address;
    private ShippingMethod shippingMethod;
    private Cart cart;
    private Variant variant;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(2);
        user.setFirstname("Karim");
        user.setLastname("Benali");

        address = new DeliveryAddress();
        address.setId(10);
        address.setFirstName("Karim");
        address.setLastName("Benali");
        address.setAddress("8 Madison Avenue");
        address.setCity("New York");
        address.setState("NY");
        address.setZipcode("10010");
        address.setCountry("US");
        address.setPhone("212-555-0102");

        shippingMethod = ShippingMethod.builder()
                .id(1)
                .name("Standard Home Delivery")
                .code("STANDARD_HOME")
                .deliveryType(DeliveryType.HOME)
                .minDeliveryDays(3)
                .maxDeliveryDays(5)
                .price(new BigDecimal("5.99"))
                .active(true)
                .build();

        cart = new Cart();
        cart.setId(5);
        cart.setUser(user);

        variant = new Variant();
        variant.setId(100);
        variant.setSku("SKU-100");
        variant.setPrice(new BigDecimal("29.99"));
        variant.setStock(10);
    }

    @Test
    void shouldCreateOrderFromCartAndClearCart() {
        CheckoutRequest request = new CheckoutRequest(10, 1, true, false, "Leave at desk");

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setVariant(variant);
        cartItem.setQuantity(2);

        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(deliveryAddressRepository.findByIdAndUserIdAndDeletedFalse(10, 2)).thenReturn(Optional.of(address));
        when(shippingMethodRepository.findByIdAndActiveTrue(1)).thenReturn(Optional.of(shippingMethod));
        when(cartRepository.findByUserId(2)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(5)).thenReturn(List.of(cartItem));
        when(shippingCalculationService.calculateShippingCost(new BigDecimal("59.98"), shippingMethod))
                .thenReturn(new BigDecimal("5.99"));
        when(taxServiceImpl.getCurrentTaxRate()).thenReturn(new BigDecimal("0.20"));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(99);
            return saved;
        });

        OrderCheckoutResponse response = orderService.checkout(2, request);

        assertThat(response.getOrderId()).isEqualTo(99);
        assertThat(response.getShippingMethodName()).isEqualTo("Standard Home Delivery");
        assertThat(response.getSubtotal()).isEqualByComparingTo("59.98");
        assertThat(response.getShippingCost()).isEqualByComparingTo("5.99");
        assertThat(response.getTaxAmount()).isEqualByComparingTo("12.00");
        assertThat(response.getTotal()).isEqualByComparingTo("77.97");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getOrderItems()).hasSize(1);
        assertThat(variant.getStock()).isEqualTo(8);

        verify(cartItemRepository).deleteByCartId(5);
    }

    @Test
    void shouldFailCheckoutWhenCartIsEmpty() {
        CheckoutRequest request = new CheckoutRequest(10, 1, false, false, null);

        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(deliveryAddressRepository.findByIdAndUserIdAndDeletedFalse(10, 2)).thenReturn(Optional.of(address));
        when(shippingMethodRepository.findByIdAndActiveTrue(1)).thenReturn(Optional.of(shippingMethod));
        when(cartRepository.findByUserId(2)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(5)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.checkout(2, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("panier est vide");
    }
}
