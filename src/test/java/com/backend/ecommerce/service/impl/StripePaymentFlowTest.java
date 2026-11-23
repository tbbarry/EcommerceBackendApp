package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.OrderItem;
import com.backend.ecommerce.entity.Payment;
import com.backend.ecommerce.entity.StockReservation;
import com.backend.ecommerce.entity.Stock;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.enums.PaymentStatus;
import com.backend.ecommerce.enums.StockReservationStatus;
import com.backend.ecommerce.event.PaymentCompletedEvent;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.PaymentRepository;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.repository.StockReservationRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.CartService;
import com.backend.ecommerce.service.PaymentService;
import com.backend.ecommerce.service.StripePaymentService;
import com.backend.ecommerce.service.StripeWebhookService;
import com.backend.ecommerce.dto.PaymentVerificationResponse;
import com.backend.ecommerce.service.StockService;
import com.backend.ecommerce.service.email.OrderNotificationService;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripePaymentFlowTest {
    @Mock OrderRepository orders;
    @Mock PaymentRepository payments;
    @Mock StockService stock;
    @Mock CartService cart;
    @Mock KafkaTemplate<String, PaymentCompletedEvent> kafka;
    @Mock PaymentService paymentService;
    @Mock StripePaymentService stripe;
    @Mock StripeWebhookService webhookService;
    @Mock StockRepository stockRepository;
    @Mock VariantRepository variants;
    @Mock StockReservationRepository reservations;
    @Mock OrderNotificationService notifications;

    Order order;
    Payment payment;
    PaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        order = Order.builder().status(OrderStatus.PENDING).total(new BigDecimal("89.00")).build();
        order.setId(1);
        payment = Payment.builder().order(order).amount(new BigDecimal("89.00"))
                .status(PaymentStatus.PENDING).stripeCheckoutSessionId("cs_test_1").build();
        service = new PaymentServiceImpl(payments, orders, kafka, stock, cart, notifications);
    }

    @Test
    void confirmsStockAndPaymentOnceEvenWhenStripeRetries() {
        when(orders.findByIdForUpdate(1)).thenReturn(Optional.of(order));
        when(payments.findByStripeCheckoutSessionId("cs_test_1")).thenReturn(Optional.of(payment));
        service.processPaymentCompleted(1, "cs_test_1", "pi_test_1", new BigDecimal("89.00"));
        service.processPaymentCompleted(1, "cs_test_1", "pi_test_1", new BigDecimal("89.00"));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertEquals("pi_test_1", payment.getStripePaymentIntentId());
        verify(stock, times(1)).confirmReservations(1);
        verify(cart, times(1)).removePurchasedItems(order);
        verify(notifications, times(1)).queuePaymentConfirmed(order, OrderStatus.PENDING);
    }

    @Test
    void rejectsWrongAmountOrSessionOwnerBeforeChangingPayment() {
        when(orders.findByIdForUpdate(1)).thenReturn(Optional.of(order));
        when(payments.findByStripeCheckoutSessionId("cs_test_1")).thenReturn(Optional.of(payment));
        assertThrows(BusinessException.class,
                () -> service.processPaymentCompleted(1, "cs_test_1", "pi_test_1", BigDecimal.ONE));
        Order other = Order.builder().build();
        other.setId(2);
        payment.setOrder(other);
        assertThrows(BusinessException.class,
                () -> service.processPaymentCompleted(1, "cs_test_1", "pi_test_1", new BigDecimal("89.00")));
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        verifyNoInteractions(stock, cart);
        verifyNoInteractions(notifications);
    }

    @Test
    void webhookRequiresPaidCompleteMatchingCurrencyAndMetadata() {
        StripeWebhookServiceImpl webhook = new StripeWebhookServiceImpl(paymentService, stripe);
        ReflectionTestUtils.setField(webhook, "currencyCode", "usd");
        Session session = new Session();
        session.setId("cs_test_1");
        session.setMode("payment");
        session.setStatus("complete");
        session.setPaymentStatus("paid");
        session.setCurrency("usd");
        session.setPaymentIntent("pi_test_1");
        session.setAmountTotal(8900L);
        session.setMetadata(Map.of("orderId", "1"));
        webhook.processPaidSession(session);
        verify(paymentService).processPaymentCompleted(1, "cs_test_1", "pi_test_1", new BigDecimal("89"));
        session.setCurrency("eur");
        assertThrows(BusinessException.class, () -> webhook.processPaidSession(session));
        session.setCurrency("usd");
        session.setPaymentStatus("unpaid");
        webhook.processPaidSession(session);
        verifyNoMoreInteractions(paymentService);
    }

    @Test
    void webhookLoadsCurrentSessionInsteadOfTrustingAnOldApiSnapshot() throws Exception {
        StripeWebhookServiceImpl webhook = new StripeWebhookServiceImpl(paymentService, stripe);
        ReflectionTestUtils.setField(webhook, "currencyCode", "usd");
        Session session = new Session();
        session.setId("cs_test_1");
        session.setMode("payment");
        session.setStatus("complete");
        session.setPaymentStatus("paid");
        session.setCurrency("usd");
        session.setPaymentIntent("pi_test_1");
        session.setAmountTotal(8900L);
        session.setMetadata(Map.of("orderId", "1"));
        when(stripe.retrieveCheckoutSession("cs_test_1")).thenReturn(session);
        var event = com.stripe.net.ApiResource.GSON.fromJson(
                "{\"id\":\"evt_test\",\"object\":\"event\",\"type\":\"checkout.session.completed\","
                + "\"api_version\":\"2018-05-21\",\"data\":{\"object\":{\"id\":\"cs_test_1\",\"object\":\"checkout.session\"}}}",
                com.stripe.model.Event.class);
        webhook.handleCheckoutSessionCompleted(event);
        verify(paymentService).processPaymentCompleted(1, "cs_test_1", "pi_test_1", new BigDecimal("89"));
    }

    @Test
    void reconciliationRetrievesTheExactSessionAndReturnsThePersistedResult() throws Exception {
        Session session = new Session();
        session.setId("cs_test_1");
        when(stripe.retrieveCheckoutSession("cs_test_1")).thenReturn(session);
        PaymentVerificationResponse response = new PaymentVerificationResponse(
                true, PaymentStatus.PAID, OrderStatus.PAID, "CMD-1", new BigDecimal("89"), 1);
        when(paymentService.verifyCheckoutSession("cs_test_1")).thenReturn(response);
        var result = new StripeReconciliationService(stripe, webhookService, paymentService).reconcile("cs_test_1");
        assertTrue(result.paid());
        verify(webhookService).processPaidSession(session);
    }

    @Test
    void reconciliationRejectsAnUnexpectedSession() throws Exception {
        Session session = new Session();
        session.setId("cs_other");
        when(stripe.retrieveCheckoutSession("cs_test_1")).thenReturn(session);
        assertThrows(BusinessException.class,
                () -> new StripeReconciliationService(stripe, webhookService, paymentService).reconcile("cs_test_1"));
        verifyNoInteractions(webhookService, paymentService);
    }

    @Test
    void refusesToConfirmPaymentWhenAnyReservationIsMissing() {
        Variant variant = new Variant();
        variant.setId(9);
        order.setOrderItems(List.of(OrderItem.builder().variant(variant).quantity(2).build()));
        when(orders.findById(1)).thenReturn(Optional.of(order));
        when(reservations.findByOrderIdAndStatus(1, StockReservationStatus.ACTIVE))
                .thenReturn(List.of(StockReservation.builder().variant(variant).quantity(1).build()));
        StockServiceImpl stocks = new StockServiceImpl(stockRepository, variants, reservations, orders);
        assertThrows(BusinessException.class, () -> stocks.confirmReservations(1));
        verifyNoInteractions(stockRepository);
    }

    @Test
    void confirmsAndDeductsTheReservedStock() {
        Variant variant = new Variant();
        variant.setId(9);
        order.setOrderItems(List.of(OrderItem.builder().variant(variant).quantity(2).build()));
        StockReservation reservation = StockReservation.builder().variant(variant).quantity(2)
                .status(StockReservationStatus.ACTIVE).build();
        Stock itemStock = Stock.builder().variant(variant).quantity(5).reservedQuantity(2).build();
        when(orders.findById(1)).thenReturn(Optional.of(order));
        when(reservations.findByOrderIdAndStatus(1, StockReservationStatus.ACTIVE)).thenReturn(List.of(reservation));
        when(stockRepository.findByVariantId(9)).thenReturn(Optional.of(itemStock));
        new StockServiceImpl(stockRepository, variants, reservations, orders).confirmReservations(1);
        assertEquals(3, itemStock.getQuantity());
        assertEquals(0, itemStock.getReservedQuantity());
        assertEquals(StockReservationStatus.CONFIRMED, reservation.getStatus());
    }
}
