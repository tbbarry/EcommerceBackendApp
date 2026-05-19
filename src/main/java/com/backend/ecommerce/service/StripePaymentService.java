package com.backend.ecommerce.service;

import com.stripe.exception.StripeException;

public interface StripePaymentService {
    String createCheckoutSession(Integer orderId) throws StripeException;
}