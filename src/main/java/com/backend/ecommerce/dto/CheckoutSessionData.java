package com.backend.ecommerce.dto;

public record CheckoutSessionData(
        String paymentUrl,
        String paymentIntentId,
        String sessionId
) {
}