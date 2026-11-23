package com.backend.ecommerce.service.impl;
import com.backend.ecommerce.service.StockService;
import com.backend.ecommerce.service.CartService;
import com.backend.ecommerce.dto.PaymentDto;
import com.backend.ecommerce.dto.PaymentVerificationResponse;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.Payment;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.enums.PaymentStatus;
import com.backend.ecommerce.event.PaymentCompletedEvent;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.PaymentRepository;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.service.PaymentService;
import com.backend.ecommerce.service.email.OrderNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j  
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;
    private final StockService stockService;
    private final CartService cartService;
    private final OrderNotificationService notifications;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> findAll() {
        return paymentRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public PaymentDto create(PaymentDto dto) {
        Payment entity = new Payment();
        applyDtoToEntity(dto, entity);
        return toDto(paymentRepository.save(entity));
    }

    @Override
    public PaymentDto update(Integer id, PaymentDto dto) {
        Payment entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(paymentRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Payment entity = getEntityById(id);
        paymentRepository.delete(entity);
    }

    private Payment getEntityById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    private void applyDtoToEntity(PaymentDto dto, Payment entity) {
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setAmount(dto.getAmount());
        entity.setPaidAt(dto.getPaidAt());
        entity.setStatus(dto.getStatus());
        if (dto.getOrderId() != null) {
            entity.setOrder(orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId())));
        } else {
            entity.setOrder(null);
        }
    }

    private PaymentDto toDto(Payment entity) {
        PaymentDto dto = new PaymentDto();
        dto.setId(entity.getId());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setAmount(entity.getAmount());
        dto.setPaidAt(entity.getPaidAt());
        dto.setStatus(entity.getStatus());
        dto.setOrderId(entity.getOrder() != null ? entity.getOrder().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    public void processSuccessfulPayment1(Integer orderId, String paymentIntentId, BigDecimal amount) {
        Order order = orderRepository.findByIdForUpdate(orderId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Order not found: " + orderId
                    )
            );

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for order: " + orderId
                        )
                );

        // Idempotence
        if (PaymentStatus.PAID.equals(payment.getStatus())) {
            return;
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setStripePaymentIntentId(paymentIntentId);

        paymentRepository.save(payment);

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        notifications.queuePaymentConfirmed(order, previousStatus);

        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .amount(amount)
                .paymentIntentId(paymentIntentId)
                .build();

        kafkaTemplate.send(
                "payment-completed",
                order.getId().toString(),
                event
        );
    }

    @Override
    @Transactional
    public void processPaymentCompleted(Integer orderId, String sessionId, String paymentIntentId, BigDecimal amount) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Commande introuvable : " + orderId,"ORDER_NOT_FOUND")
                );
        Payment payment = paymentRepository
                .findByStripeCheckoutSessionId(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Paiement introuvable pour cette session Stripe.",
                                "PAYMENT_NOT_FOUND"
                        )
                );
        if (!orderId.equals(payment.getOrder().getId())
                || order.getTotal().compareTo(amount) != 0
                || payment.getAmount().compareTo(amount) != 0
                || (payment.getStripePaymentIntentId() != null
                    && !payment.getStripePaymentIntentId().equals(paymentIntentId))) {
            throw new BusinessException(
                    "La session Stripe ne correspond pas au paiement de la commande.",
                    "PAYMENT_SESSION_MISMATCH"
            );
        }
        if (PaymentStatus.PAID.equals(payment.getStatus())
                && (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.PROCESSING
                    || order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED)) {
            return;
        }
        if (payment.getStatus() != PaymentStatus.PENDING || order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Les statuts de la commande et du paiement nécessitent une vérification.",
                    "INVALID_PAYMENT_STATE");
        }

        stockService.confirmReservations(order.getId());
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setStripePaymentIntentId(paymentIntentId);
        order.setStatus(OrderStatus.PAID);
        paymentRepository.save(payment);
        orderRepository.save(order);
        cartService.removePurchasedItems(order);
        notifications.queuePaymentConfirmed(order, OrderStatus.PENDING);
        log.info("Paiement confirmé pour la commande {}", order.getId());
    }


    @Override
    @Transactional(readOnly = true)
    public PaymentVerificationResponse verifyCheckoutSession(String sessionId) {

        Payment payment =
                paymentRepository
                        .findByStripeCheckoutSessionId(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found for checkout session: "
                                                + sessionId,
                                        "PAYMENT_NOT_FOUND"
                                )
                        );

        boolean paid =PaymentStatus.PAID.equals(payment.getStatus());
        
        return new PaymentVerificationResponse(
                paid,
                payment.getStatus(),
                payment.getOrder().getStatus(),
                payment.getOrder().getOrderNumber(),
                payment.getAmount(),
                payment.getOrder().getId()
        );
    }
}
