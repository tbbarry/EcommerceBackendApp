package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ProductDetailResponse;
import com.backend.ecommerce.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductDetailService productDetailService;

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductDetailResponse> findDetailsBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productDetailService.getBySlug(slug));
    }
}
