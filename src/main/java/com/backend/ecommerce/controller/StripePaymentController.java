package com.backend.ecommerce.controller;

import com.backend.ecommerce.service.StripePaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/stripe")
@RequiredArgsConstructor
public class StripePaymentController {

    private final StripePaymentService stripePaymentService;

    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<String> createCheckout(@PathVariable Integer orderId) throws StripeException {
        return ResponseEntity.ok(stripePaymentService.createCheckoutSession(orderId));
    }
}