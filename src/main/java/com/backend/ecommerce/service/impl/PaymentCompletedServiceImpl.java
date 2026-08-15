package com.backend.ecommerce.service.impl;
import com.backend.ecommerce.enums.PaymentStatus;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.service.CartService;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.Payment;
import com.backend.ecommerce.event.PaymentCompletedEvent;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.PaymentRepository;
import com.backend.ecommerce.service.PaymentCompletedService;
import com.backend.ecommerce.service.StockService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletedServiceImpl implements PaymentCompletedService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final StockService stockService;
    private final CartService cartService;


    @Override
    @Transactional
    public void processPaymentCompleted(PaymentCompletedEvent event) {

        log.info("Traitement du paiement terminé");
        log.info("Order ID : {}", event.getOrderId());
        log.info("User ID : {}", event.getUserId());
        log.info("Payment Intent ID : {}", event.getPaymentIntentId());
        log.info("Amount : {}", event.getAmount());

        // 1. Récupérer la commande
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Commande introuvable : " + event.getOrderId(),"ORDER_NOT_FOUND")
                );

        // 2. Vérifier si la commande est déjà payée
        if (OrderStatus.PAID.equals(order.getStatus())) {
            log.info(
                    "La commande {} est déjà payée. Événement ignoré.",
                    order.getId()
            );
            return;
        }

        // 3. Récupérer le paiement via le PaymentIntent Stripe
        Payment payment = paymentRepository
                .findByStripePaymentIntentId(event.getPaymentIntentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Paiement introuvable pour le PaymentIntent : "
                                        + event.getPaymentIntentId(),
                                "PAYMENT_NOT_FOUND"
                        )
                );
        if (order.getTotal().compareTo(event.getAmount()) != 0) {
            throw new BusinessException(
                    "Le montant du paiement ne correspond pas au total de la commande",
                    "PAYMENT_AMOUNT_MISMATCH"
            );
        }

        // 4. Mettre à jour le paiement
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        // 5. Mettre à jour la commande
        order.setStatus(OrderStatus.PAID);
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
}

