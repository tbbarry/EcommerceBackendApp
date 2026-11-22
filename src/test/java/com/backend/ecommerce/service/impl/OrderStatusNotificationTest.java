package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.OrderDto;
import com.backend.ecommerce.dto.OrderItemDto;
import com.backend.ecommerce.entity.DeliveryAddress;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.repository.DeliveryAddressRepository;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.email.OrderNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusNotificationTest {
    @Mock OrderRepository orders;
    @Mock UserRepository users;
    @Mock DeliveryAddressRepository addresses;
    @Mock VariantRepository variants;
    @Mock OrderNotificationService notifications;
    @InjectMocks OrderServiceImpl service;

    @Test
    void legacyAdminUpdateQueuesTheActualStatusChangeUnderLock() {
        User user = User.builder().firstname("Léa").email("lea@example.test").build();
        user.setId(2);
        DeliveryAddress address = DeliveryAddress.builder().country("FR").build();
        address.setId(3);
        Order order = Order.builder().status(OrderStatus.PROCESSING).taxRate(new BigDecimal("0.20")).build();
        order.setId(1);
        Variant variant = Variant.builder().price(BigDecimal.TEN).build();
        variant.setId(4);
        OrderItemDto item = new OrderItemDto();
        item.setVariantId(4);
        item.setQuantity(1);
        OrderDto request = new OrderDto();
        request.setOrderNumber("CMD-1");
        request.setUserId(2);
        request.setDeliveryAddressId(3);
        request.setStatus(OrderStatus.SHIPPED);
        request.setItems(List.of(item));
        when(orders.findByIdForUpdate(1)).thenReturn(Optional.of(order));
        when(users.findById(2)).thenReturn(Optional.of(user));
        when(addresses.findByIdAndDeletedFalse(3)).thenReturn(Optional.of(address));
        when(variants.findById(4)).thenReturn(Optional.of(variant));
        when(orders.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(OrderStatus.SHIPPED, service.update(1, request).getStatus());
        verify(notifications).queueStatusChange(order, OrderStatus.PROCESSING);
    }
}
