package com.backend.ecommerce.service.impl;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.service.StripeWebhookService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.backend.ecommerce.service.PaymentService;

import java.math.BigDecimal;


import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j  
public class StripeWebhookServiceImpl implements StripeWebhookService {

    private final PaymentService paymentService;

    @Override
    public void handleCheckoutSessionCompleted(Event event) {
        log.info("Received Stripe webhook event: {}", event.getType());
        log.info("Event ID: {}", event.getId());
        if(!"checkout.session.completed".equals(event.getType())) {
            return;
        }

        System.out.println("Handling checkout.session.completed event");
        Session session = ApiResource.GSON.fromJson(
                event.getDataObjectDeserializer().getRawJson(),
                Session.class
        );
        if(!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            log.info("Payment status is not 'paid', ignoring the event.");
            return;
        }
        Integer  orderId    = session.getMetadata().get("orderId") != null ? Integer.valueOf(session.getMetadata().get("orderId")) : null;
        if(orderId == null) {
            throw new ResourceNotFoundException("Order ID not found in session metadata", "ORDER_ID_NOT_FOUND");
        }
        log.info("Processing payment for order ID: {}", orderId);
        String paymentIntentId  = session.getPaymentIntent();
        BigDecimal amount = BigDecimal.valueOf(session.getAmountTotal()).divide(BigDecimal.valueOf(100));
        
        paymentService.processPaymentCompleted(orderId, paymentIntentId, amount);
    }
}
        