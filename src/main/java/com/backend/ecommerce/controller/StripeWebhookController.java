package com.backend.ecommerce.controller;

import com.backend.ecommerce.service.StripeWebhookService;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;


    private final StripeWebhookService stripeWebhookService;

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
        stripeWebhookService.handleCheckoutSessionCompleted(event);
        return ResponseEntity.ok("success");
    }
}
