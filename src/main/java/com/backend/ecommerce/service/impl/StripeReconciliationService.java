package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.PaymentVerificationResponse;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.service.PaymentService;
import com.backend.ecommerce.service.StripePaymentService;
import com.backend.ecommerce.service.StripeWebhookService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeReconciliationService {
    private final StripePaymentService stripe;
    private final StripeWebhookService webhook;
    private final PaymentService payments;

    public PaymentVerificationResponse reconcile(String sessionId) {
        try {
            Session session = stripe.retrieveCheckoutSession(sessionId);
            if (session == null || !sessionId.equals(session.getId())) {
                throw new BusinessException("Session Stripe invalide.", "INVALID_STRIPE_SESSION");
            }
            webhook.processPaidSession(session);
            return payments.verifyCheckoutSession(sessionId);
        } catch (StripeException e) {
            log.error("Impossible de vérifier la session de paiement auprès de Stripe", e);
            throw new BusinessException("Impossible de vérifier le paiement auprès de Stripe. Réessayez plus tard.",
                    "STRIPE_VERIFICATION_FAILED", e);
        }
    }
}
