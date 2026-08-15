package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    Optional<Payment> findByOrderId(Integer orderId);
    Optional<Payment> findByStripeCheckoutSessionId(String stripeCheckoutSessionId);
}
