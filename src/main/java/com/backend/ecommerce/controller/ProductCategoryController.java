package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ProductCategoryDto;
import com.backend.ecommerce.service.ProductCategoryService;
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
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @GetMapping
    public ResponseEntity<List<ProductCategoryDto>> findAll() {
        return ResponseEntity.ok(productCategoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(productCategoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductCategoryDto> create(@Valid @RequestBody ProductCategoryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productCategoryService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryDto> update(@PathVariable Integer id, @Valid @RequestBody ProductCategoryDto dto) {
        return ResponseEntity.ok(productCategoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

