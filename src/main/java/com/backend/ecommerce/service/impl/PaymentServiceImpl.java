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
        Order order = orderRepository.findById(orderId)
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

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

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
    public void processPaymentCompleted(Integer orderId, String paymentIntentId, BigDecimal amount) {

        log.info("Payment Intent ID: {}", paymentIntentId);
        log.info("***************************************************************");
        log.info("Traitement du paiement terminé");
        log.info("***************************************************************");
        // 1. Récupérer la commande 
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Commande introuvable : " + orderId,"ORDER_NOT_FOUND")
                );
        log.info("Commande récupérée avec succès : {}", order.getId());
        log.info("####################################################################");
        // 2. Vérifier si la commande est déjà payée
        if (OrderStatus.PAID.equals(order.getStatus())) {
            log.info(
                    "La commande {} est déjà payée. Événement ignoré.",
                    order.getId()
            );
            return;
        }
        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Paiement introuvable pour la commande : " + orderId,
                                "PAYMENT_NOT_FOUND"
                        )
                );

        if (PaymentStatus.PAID.equals(payment.getStatus())) {
            log.info(
                    "Le paiement pour la commande {} est déjà marqué comme PAID. Événement ignoré.",
                    order.getId()
            );
            throw new BusinessException(
                    "Le paiement pour cette commande est déjà marqué comme PAID",
                    "PAYMENT_ALREADY_PAID"
            );
        }
        log.info("Paiement récupéré avec succès pour le PaymentIntent : {} avec le montant {}", paymentIntentId, amount);

        if (order.getTotal().compareTo(amount) != 0) {
            log.error(
                    "Le montant du paiement ({}) ne correspond pas au total de la commande ({}).",
                    amount,
                    order.getTotal()
            );
            throw new BusinessException(
                    "Le montant du paiement ne correspond pas au total de la commande",
                    "PAYMENT_AMOUNT_MISMATCH"
            );
        }

        // 4. Mettre à jour le paiement
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setStripePaymentIntentId(paymentIntentId);
        // 5. Mettre à jour la commande
        order.setStatus(OrderStatus.PAID);
        log.info("####################################################################");
        log.info("Mise à jour du paiement avec le statut PAID et la date de paiement pour le PaymentIntent {} et la commande {}", paymentIntentId, order.getId());

        // 6. Confirmer les réservations de stock associées à la commande
        stockService.confirmReservations(order.getId());

        paymentRepository.save(payment);
        orderRepository.save(order);
        log.info("Paiement et commande mis à jour avec succès pour la commande {}", order.getId());
        log.info("Confirmation des réservations de stock pour la commande {}", order.getId());
        log.info("Nettoyage du panier pour l'utilisateur {}", order.getUser().getId());
        cartService.clearCartView(order.getUser().getId());
        log.info("traitement du paiement terminé pour la commande {}", order.getId());
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
                payment.getAmount()
        );
    }
}
