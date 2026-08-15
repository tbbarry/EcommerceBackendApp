package com.backend.ecommerce.service;

import java.math.BigDecimal;

import com.backend.ecommerce.dto.PaymentDto;
import com.backend.ecommerce.dto.PaymentVerificationResponse;

public interface PaymentService extends CrudService<PaymentDto, Integer> {
void processSuccessfulPayment1(Integer orderId, String paymentIntentId, BigDecimal amount);
void processPaymentCompleted(Integer orderId,  String paymentIntentId, BigDecimal amount);
PaymentVerificationResponse verifyCheckoutSession(String sessionId);

}
