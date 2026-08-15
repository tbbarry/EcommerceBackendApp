package com.backend.ecommerce.consumer;

import com.backend.ecommerce.event.PaymentCompletedEvent;
import com.backend.ecommerce.service.PaymentCompletedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final PaymentCompletedService paymentCompletedService;

    @KafkaListener(
            topics = "payment-completed",
            groupId = "payment-group"
    )
    public void consume(PaymentCompletedEvent event) {

        paymentCompletedService.processPaymentCompleted(event);
    }
}