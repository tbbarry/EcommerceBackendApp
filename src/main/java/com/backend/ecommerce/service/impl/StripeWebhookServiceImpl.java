package com.backend.ecommerce.service.impl;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.service.StripeWebhookService;
import com.backend.ecommerce.service.StripePaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.backend.ecommerce.service.PaymentService;

import java.math.BigDecimal;


import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
@Service
@RequiredArgsConstructor
@Slf4j  
public class StripeWebhookServiceImpl implements StripeWebhookService {

    private final PaymentService paymentService;
    private final StripePaymentService stripe;
    @Value("${currency.code}")
    private String currencyCode;

    @Override
    public void handleCheckoutSessionCompleted(Event event) {
        log.info("Received Stripe webhook event: {}", event.getType());
        log.info("Event ID: {}", event.getId());
        if (!"checkout.session.completed".equals(event.getType())
                && !"checkout.session.async_payment_succeeded".equals(event.getType())) {
            return;
        }

        Session snapshot = ApiResource.GSON.fromJson(
                event.getDataObjectDeserializer().getRawJson(),
                Session.class
        );
        if (snapshot == null || snapshot.getId() == null || snapshot.getId().isBlank()) {
            throw new BusinessException("Session Stripe invalide.", "INVALID_STRIPE_SESSION");
        }
        try {
            Session session = stripe.retrieveCheckoutSession(snapshot.getId());
            if (session == null || !snapshot.getId().equals(session.getId())) {
                throw new BusinessException("Session Stripe invalide.", "INVALID_STRIPE_SESSION");
            }
            processPaidSession(session);
        } catch (StripeException e) {
            log.error("Impossible de vérifier une session Stripe reçue par webhook", e);
            throw new BusinessException("Impossible de vérifier le paiement auprès de Stripe.",
                    "STRIPE_VERIFICATION_FAILED", e);
        }
    }

    @Override
    public void processPaidSession(Session session) {
        if (session == null) {
            throw new BusinessException("Session Stripe invalide.", "INVALID_STRIPE_SESSION");
        }
        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            log.info("Payment status is not 'paid', ignoring the event.");
            return;
        }
        if (!"payment".equals(session.getMode()) || !"complete".equals(session.getStatus())
                || session.getId() == null || session.getId().isBlank()
                || session.getPaymentIntent() == null || session.getPaymentIntent().isBlank()
                || session.getAmountTotal() == null || !currencyCode.equalsIgnoreCase(session.getCurrency())) {
            throw new BusinessException("Les données du paiement Stripe sont invalides.", "INVALID_STRIPE_SESSION");
        }
        String metadataOrderId = session.getMetadata() == null ? null : session.getMetadata().get("orderId");
        if (metadataOrderId == null) {
            throw new ResourceNotFoundException("Order ID not found in session metadata", "ORDER_ID_NOT_FOUND");
        }
        Integer orderId;
        try {
            orderId = Integer.valueOf(metadataOrderId);
        } catch (NumberFormatException e) {
            throw new BusinessException("Identifiant de commande Stripe invalide.", "INVALID_STRIPE_ORDER_ID");
        }
        log.info("Processing payment for order ID: {}", orderId);
        String paymentIntentId  = session.getPaymentIntent();
        BigDecimal amount = BigDecimal.valueOf(session.getAmountTotal()).divide(BigDecimal.valueOf(100));
        
        try {
            paymentService.processPaymentCompleted(orderId, session.getId(), paymentIntentId, amount);
        } catch (BusinessException e) {
            log.error("Paiement Stripe reçu mais commande {} non confirmée : {}", orderId, e.getCode());
            throw e;
        }
    }
}
        