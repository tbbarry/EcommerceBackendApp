package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ReviewDtos;
import com.backend.ecommerce.service.SecurityService;
import com.backend.ecommerce.service.impl.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ProductReviewService reviews;
    private final SecurityService security;

    @GetMapping("/api/products/{productId}/reviews")
    public ReviewDtos.Summary publicReviews(@PathVariable Integer productId,
                                             @RequestParam(defaultValue = "0") int page) {
        return reviews.publicReviews(productId, page);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/api/reviews/order-items/{itemId}")
    public ResponseEntity<ReviewDtos.Review> mine(@PathVariable Integer itemId) {
        ReviewDtos.Review review = reviews.mine(itemId, security.getCurrentUserIdOrThrow());
        return review == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(review);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/api/reviews/order-items/{itemId}")
    public ReviewDtos.Review save(@PathVariable Integer itemId, @Valid @RequestBody ReviewDtos.Request request) {
        return reviews.save(itemId, security.getCurrentUserIdOrThrow(), request);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/api/reviews/order-items/{itemId}")
    public ResponseEntity<Void> delete(@PathVariable Integer itemId) {
        reviews.deleteMine(itemId, security.getCurrentUserIdOrThrow());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/reviews")
    public Page<ReviewDtos.Review> adminReviews(@RequestParam(defaultValue = "0") int page) {
        return reviews.adminReviews(page);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/api/admin/reviews/{id}/visibility")
    public ReviewDtos.Review visibility(@PathVariable Integer id, @Valid @RequestBody ReviewDtos.VisibilityRequest request) {
        return reviews.setVisibility(id, request.visible());
    }
}
