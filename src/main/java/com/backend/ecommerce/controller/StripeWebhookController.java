package com.backend.ecommerce.controller;

import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.Payment;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {

        System.out.println("WEBHOOK STRIPE APPELE");
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            System.out.println("EVENT TYPE = " + event.getType());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {

            Session session = ApiResource.GSON.fromJson(
                    event.getDataObjectDeserializer().getRawJson(),
                    Session.class
            );

            System.out.println("SESSION ID = " + session.getId());
            System.out.println("METADATA = " + session.getMetadata());

            Integer orderId = Integer.valueOf(session.getMetadata().get("orderId"));

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

            order.setStatus("PAID");
            orderRepository.save(order);

            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setPaymentMethod("CARD");
            payment.setStatus("PAID");
            payment.setAmount(BigDecimal.valueOf(session.getAmountTotal()).divide(BigDecimal.valueOf(100)));
            payment.setPaidAt(LocalDateTime.now());

            paymentRepository.save(payment);
        }

        return ResponseEntity.ok("success");
    }
}
