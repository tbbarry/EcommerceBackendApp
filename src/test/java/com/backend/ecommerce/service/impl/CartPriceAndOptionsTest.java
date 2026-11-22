package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.entity.Attribute;
import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.entity.Cart;
import com.backend.ecommerce.entity.CartItem;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.OrderItem;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.entity.VariantAttributeValue;
import com.backend.ecommerce.repository.CartItemRepository;
import com.backend.ecommerce.repository.CartRepository;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.PromotionPricingService;
import com.backend.ecommerce.service.TaxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CartPriceAndOptionsTest {
    @Mock CartRepository carts;
    @Mock UserRepository users;
    @Mock CartItemRepository items;
    @Mock VariantRepository variants;
    @Mock ProductImageRepository images;
    @Mock TaxService tax;
    @Mock PromotionPricingService pricing;
    @InjectMocks CartServiceImpl service;

    @Test
    void cartReturnsBaseAndPromotionalPricesAndActualVariantOptions() {
        User user = new User();
        user.setId(1);
        Cart cart = new Cart();
        cart.setId(2);
        cart.setUser(user);
        Product product = Product.builder().name("Article").build();
        product.setId(3);
        AttributeValue color = AttributeValue.builder()
                .attribute(Attribute.builder().name("Couleur").build()).value("Bleu").build();
        Variant variant = Variant.builder().product(product).price(new BigDecimal("50.00"))
                .attributeValues(List.of(VariantAttributeValue.builder().attributeValue(color).build())).build();
        variant.setId(4);
        CartItem item = CartItem.builder().variant(variant).quantity(2).build();
        when(users.findById(1)).thenReturn(Optional.of(user));
        when(carts.findByUserId(1)).thenReturn(Optional.of(cart));
        when(items.findByCartId(2)).thenReturn(List.of(item));
        when(pricing.promotionalUnitPrices(any(), any())).thenReturn(Map.of(4, new BigDecimal("45.00")));
        when(images.findByProductIdIn(List.of(3))).thenReturn(List.of());
        when(tax.getCurrentTaxRate()).thenReturn(new BigDecimal("0.20"));

        var line = service.getCartViewByUserId(1).getItems().get(0);

        assertEquals(new BigDecimal("50.00"), line.getBaseUnitPrice());
        assertEquals(new BigDecimal("45.00"), line.getUnitPrice());
        assertEquals("Bleu", line.getColor());
        assertNull(line.getSize());
    }

    @Test
    void paymentRemovesOnlyUnchangedPurchasedItems() {
        User user = new User();
        user.setId(1);
        Cart cart = new Cart();
        cart.setId(2);
        Variant purchased = new Variant();
        purchased.setId(4);
        Variant later = new Variant();
        later.setId(5);
        LocalDateTime checkout = LocalDateTime.of(2026, 1, 1, 10, 0);
        CartItem original = CartItem.builder().variant(purchased).quantity(1).build();
        original.setUpdatedAt(checkout.minusMinutes(1));
        CartItem addedLater = CartItem.builder().variant(later).quantity(1).build();
        addedLater.setUpdatedAt(checkout.plusMinutes(1));
        CartItem changedLater = CartItem.builder().variant(purchased).quantity(1).build();
        changedLater.setUpdatedAt(checkout.plusMinutes(1));
        Order order = Order.builder().user(user).dateOrder(checkout)
                .orderItems(List.of(OrderItem.builder().variant(purchased).quantity(1).build())).build();
        when(carts.findByUserId(1)).thenReturn(Optional.of(cart));
        when(items.findByCartId(2)).thenReturn(List.of(original, addedLater, changedLater));
        service.removePurchasedItems(order);
        verify(items).delete(original);
        verify(items, never()).delete(addedLater);
        verify(items, never()).delete(changedLater);
    }
}
