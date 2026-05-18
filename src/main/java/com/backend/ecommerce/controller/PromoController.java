package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.PromoDto;
import com.backend.ecommerce.service.PromoService;
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
@RequestMapping("/api/promos")
@RequiredArgsConstructor
public class PromoController {

    private final PromoService promoService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<PromoDto>> findAllActive() {
        return ResponseEntity.ok(promoService.findAllActive());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<PromoDto>> findAllForAdmin() {
        return ResponseEntity.ok(promoService.findAll());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PromoDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(promoService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PromoDto> create(@Valid @RequestBody PromoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promoService.create(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PromoDto> update(@PathVariable Integer id, @Valid @RequestBody PromoDto dto) {
        return ResponseEntity.ok(promoService.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        promoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}