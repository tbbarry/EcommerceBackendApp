package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.VariantDto;
import com.backend.ecommerce.service.VariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/variants")
@RequiredArgsConstructor
public class VariantController {

    private final VariantService variantService;

    @GetMapping
    public ResponseEntity<List<VariantDto>> findAll() {
        return ResponseEntity.ok(variantService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VariantDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(variantService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VariantDto> create(@Valid @RequestBody VariantDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(variantService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VariantDto> update(@PathVariable Integer id, @Valid @RequestBody VariantDto dto) {
        return ResponseEntity.ok(variantService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        variantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

