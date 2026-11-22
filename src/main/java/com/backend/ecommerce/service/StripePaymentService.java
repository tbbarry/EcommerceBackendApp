package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CheckoutSessionData;
import com.backend.ecommerce.entity.Order;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

public interface StripePaymentService {
    CheckoutSessionData createCheckoutSession(Order order) throws StripeException;
    Session retrieveCheckoutSession(String sessionId) throws StripeException;
}