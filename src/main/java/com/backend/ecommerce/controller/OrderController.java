package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.CheckoutRequest;
import com.backend.ecommerce.dto.OrderCheckoutResponse;
import com.backend.ecommerce.dto.OrderDto;
import com.backend.ecommerce.service.OrderService;
import com.backend.ecommerce.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SecurityService securityService;

    // ADMIN uniquement : voir toutes les commandes
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderDto>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats/total-amount-paid")
    public ResponseEntity<BigDecimal> getTotalPaidOrdersAmount() {
        return ResponseEntity.ok(orderService.getTotalPaidOrdersAmount());
    }

    // ADMIN ou propriétaire de la commande
    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessOrder(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    // USER ou ADMIN : créer une commande
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(dto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/checkout")
    public ResponseEntity<OrderCheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        Integer userId = securityService.getCurrentUserIdOrThrow();
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.checkout(userId, request));
    }

    // ADMIN uniquement : modifier une commande
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@PathVariable Integer id, @Valid @RequestBody OrderDto dto) {
        return ResponseEntity.ok(orderService.update(id, dto));
    }

    // ADMIN uniquement : supprimer une commande
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

