package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.PaymentDto;
import com.backend.ecommerce.service.PaymentService;
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

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ADMIN uniquement : voir tous les paiements
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PaymentDto>> findAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    // ADMIN ou propriétaire de la commande liée au paiement
    @PreAuthorize("hasRole('ADMIN') or @securityService.isPaymentOwner(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    // ADMIN ou propriétaire de la commande
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderOwner(#dto.orderId)")
    @PostMapping
    public ResponseEntity<PaymentDto> create(@Valid @RequestBody PaymentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(dto));
    }

    // ADMIN uniquement : modifier le statut/montant d’un paiement
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> update(@PathVariable Integer id, @Valid @RequestBody PaymentDto dto) {
        return ResponseEntity.ok(paymentService.update(id, dto));
    }

    // ADMIN uniquement
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}