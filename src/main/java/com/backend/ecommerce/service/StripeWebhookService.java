package com.backend.ecommerce.service;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;

public interface StripeWebhookService {

    void handleCheckoutSessionCompleted(Event event);
    void processPaidSession(Session session);
}
