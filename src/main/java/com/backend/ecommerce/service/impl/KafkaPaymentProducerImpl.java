package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.event.PaymentCompletedEvent;
import com.backend.ecommerce.service.KafkaPaymentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPaymentProducerImpl implements KafkaPaymentProducer {

    private static final String TOPIC = "payment-completed";

    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;

    @Override
    public void sendPaymentCompleted(PaymentCompletedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getOrderId().toString(),
                event
        );
    }
}