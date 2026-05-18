package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.InvoiceDto;
import com.backend.ecommerce.service.InvoiceService;
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
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // ADMIN uniquement : voir toutes les factures
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<InvoiceDto>> findAll() {
        return ResponseEntity.ok(invoiceService.findAll());
    }

    // ADMIN ou propriétaire de la facture
    @PreAuthorize("hasRole('ADMIN') or @securityService.isInvoiceOwner(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }

    // ADMIN uniquement : création manuelle d'une facture
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<InvoiceDto> create(@Valid @RequestBody InvoiceDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.create(dto));
    }

    // ADMIN uniquement : modifier une facture
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDto> update(@PathVariable Integer id, @Valid @RequestBody InvoiceDto dto) {
        return ResponseEntity.ok(invoiceService.update(id, dto));
    }

    // ADMIN uniquement : supprimer une facture
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

