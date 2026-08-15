package com.backend.ecommerce.dto;

import com.backend.ecommerce.enums.PaymentStatus;

import java.math.BigDecimal;

import com.backend.ecommerce.enums.OrderStatus;

public record PaymentVerificationResponse(
        boolean paid,
        PaymentStatus paymentStatus,
        OrderStatus orderStatus,
        String orderId,
        BigDecimal amount
) {
}