package com.backend.ecommerce.service;

import com.stripe.model.Event;

public interface StripeWebhookService {

    void handleCheckoutSessionCompleted(Event event);
}
