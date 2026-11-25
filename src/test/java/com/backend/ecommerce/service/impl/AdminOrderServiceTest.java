package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.AdminOrderStatusRequest;
import com.backend.ecommerce.dto.OrderDto;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.Payment;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.enums.PaymentStatus;
import com.backend.ecommerce.event.PaymentCompletedEvent;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.PaymentRepository;
import com.backend.ecommerce.service.CartService;
import com.backend.ecommerce.service.OrderService;
import com.backend.ecommerce.service.StockService;
import com.backend.ecommerce.service.email.OrderNotificationService;
import org.springframework.kafka.core.KafkaTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {
    @Mock OrderRepository repository;
    @Mock OrderService orders;
    @Mock PaymentRepository payments;
    @Mock StockService stock;
    @Mock CartService cart;
    @Mock KafkaTemplate<String, PaymentCompletedEvent> kafka;
    @Mock OrderNotificationService notifications;
    AdminOrderService service;
    Order order;

    @BeforeEach
    void setUp() {
        service = new AdminOrderService(repository, orders, notifications);
        order = Order.builder().status(OrderStatus.PAID).user(User.builder().email("client@example.com").build()).build();
        order.setId(1);
    }

    @Test
    void advancesOnlyInOrderAndReturnsUpdatedDetail() {
        when(repository.findByIdForUpdate(1)).thenReturn(Optional.of(order));
        when(orders.findById(1)).thenAnswer(invocation -> {
            OrderDto dto = new OrderDto();
            dto.setStatus(order.getStatus());
            return dto;
        });
        var processing = service.advance(1, new AdminOrderStatusRequest(OrderStatus.PAID, OrderStatus.PROCESSING));
        assertEquals(OrderStatus.PROCESSING, processing.order().getStatus());
        assertEquals("client@example.com", processing.customerEmail());
        service.advance(1, new AdminOrderStatusRequest(OrderStatus.PROCESSING, OrderStatus.SHIPPED));
        service.advance(1, new AdminOrderStatusRequest(OrderStatus.SHIPPED, OrderStatus.DELIVERED));
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        verify(repository, times(3)).save(order);
        verify(notifications).queueStatusChange(order, OrderStatus.PAID);
        verify(notifications).queueStatusChange(order, OrderStatus.PROCESSING);
        verify(notifications).queueStatusChange(order, OrderStatus.SHIPPED);
    }

    @Test
    void rejectsUnpaidInvalidAndStaleTransitions() {
        when(repository.findByIdForUpdate(1)).thenReturn(Optional.of(order));
        assertThrows(BusinessException.class,
                () -> service.advance(1, new AdminOrderStatusRequest(OrderStatus.PAID, OrderStatus.SHIPPED)));
        assertThrows(BusinessException.class,
                () -> service.advance(1, new AdminOrderStatusRequest(OrderStatus.PROCESSING, OrderStatus.SHIPPED)));
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        assertThrows(BusinessException.class,
                () -> service.advance(1, new AdminOrderStatusRequest(OrderStatus.PENDING_PAYMENT, OrderStatus.PROCESSING)));
        verify(repository, never()).save(any());
        verifyNoInteractions(notifications);
    }

    @Test
    void duplicatePaymentEventDoesNotRewindFulfillmentOrConfirmStockAgain() {
        order.setStatus(OrderStatus.SHIPPED);
        when(repository.findByIdForUpdate(1)).thenReturn(Optional.of(order));
        new PaymentCompletedServiceImpl(repository, payments, stock, cart, notifications)
                .processPaymentCompleted(PaymentCompletedEvent.builder().orderId(1).build());
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        verifyNoInteractions(payments, stock, cart, notifications);
    }

    @Test
    void duplicateWebhookDoesNotRewindFulfillmentOrConfirmStockAgain() {
        order.setStatus(OrderStatus.PROCESSING);
        order.setTotal(java.math.BigDecimal.TEN);
        Payment payment = Payment.builder().order(order).amount(java.math.BigDecimal.TEN)
                .status(PaymentStatus.PAID).stripePaymentIntentId("intent").build();
        when(repository.findByIdForUpdate(1)).thenReturn(Optional.of(order));
        when(payments.findByStripeCheckoutSessionId("session")).thenReturn(Optional.of(payment));
        new PaymentServiceImpl(payments, repository, kafka, stock, cart, notifications)
                .processPaymentCompleted(1, "session", "intent", java.math.BigDecimal.TEN);
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        verifyNoInteractions(stock, cart, kafka, notifications);
    }
}
