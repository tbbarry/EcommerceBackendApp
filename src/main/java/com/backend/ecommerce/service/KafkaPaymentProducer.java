package com.backend.ecommerce.service;

import com.backend.ecommerce.event.PaymentCompletedEvent;

public interface KafkaPaymentProducer {

    void sendPaymentCompleted(PaymentCompletedEvent event);
}