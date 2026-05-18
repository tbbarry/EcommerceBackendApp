package com.backend.ecommerce.service;

import com.backend.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public boolean isCurrentUser(Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }

    public boolean isDeliveryAddressOwner(Integer addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();

        return deliveryAddressRepository.findById(addressId)
                .map(address -> address.getUser().getEmail().equals(email))
                .orElse(false);
    }

    public boolean isInvoiceOwner(Integer invoiceId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();

        return invoiceRepository.findById(invoiceId)
                .map(invoice -> invoice.getOrder().getUser().getEmail().equals(email))
                .orElse(false);
    }

    public boolean canAccessOrder(Integer orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();

        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getEmail().equals(email))
                .orElse(false);
    }

    public boolean isOrderOwner(Integer orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();

        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getEmail().equals(email))
                .orElse(false);
    }

    public boolean isPaymentOwner(Integer paymentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();

        return paymentRepository.findById(paymentId)
                .map(payment -> payment.getOrder().getUser().getEmail().equals(email))
                .orElse(false);
    }
}
