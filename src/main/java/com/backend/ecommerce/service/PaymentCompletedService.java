package com.backend.ecommerce.service;

import com.backend.ecommerce.event.PaymentCompletedEvent;

public interface PaymentCompletedService {

    void processPaymentCompleted(PaymentCompletedEvent event);
}