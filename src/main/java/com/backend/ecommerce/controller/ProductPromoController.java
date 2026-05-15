package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ProductPromoDto;
import com.backend.ecommerce.service.ProductPromoService;
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
@RequestMapping("/api/product-promos")
@RequiredArgsConstructor
public class ProductPromoController {

    private final ProductPromoService productPromoService;

    @GetMapping
    public ResponseEntity<List<ProductPromoDto>> findAll() {
        return ResponseEntity.ok(productPromoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductPromoDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(productPromoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductPromoDto> create(@Valid @RequestBody ProductPromoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productPromoService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductPromoDto> update(@PathVariable Integer id, @Valid @RequestBody ProductPromoDto dto) {
        return ResponseEntity.ok(productPromoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productPromoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
