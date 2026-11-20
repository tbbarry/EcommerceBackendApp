package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ProductDetailResponse;
import com.backend.ecommerce.dto.ProductCreateRequest;
import com.backend.ecommerce.dto.ProductFullResponse;
import com.backend.ecommerce.service.ProductDetailService;
import com.backend.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductDetailService productDetailService;
    private final ProductService productService;

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductDetailResponse> findDetailsBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productDetailService.getBySlug(slug));
    }
    
    // ADMIN : créer un produit avec variantes et images
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductFullResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        System.out.println("Creating product with request: " + request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProductFull(request));
    }
    
    // PUBLIC : récupérer un produit avec toutes ses variantes et images
    @GetMapping("/{id}/full")
    public ResponseEntity<ProductFullResponse> getProductFull(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductFull(id));
    }
}

