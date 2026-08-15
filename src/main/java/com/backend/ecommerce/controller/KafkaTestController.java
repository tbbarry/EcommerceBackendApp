package com.backend.ecommerce.controller;

import com.backend.ecommerce.event.PaymentCompletedEvent;
import com.backend.ecommerce.service.KafkaPaymentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/test/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaPaymentProducer kafkaPaymentProducer;

    @PostMapping("/payment-completed")
    public ResponseEntity<String> testPaymentCompleted() {

        PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                .orderId(1)
                .userId(1)
                .paymentIntentId("pi_test_123")
                .amount(new BigDecimal("49.99"))
                .build();

        kafkaPaymentProducer.sendPaymentCompleted(event);

        return ResponseEntity.ok("PaymentCompletedEvent envoyé à Kafka");
    }
}