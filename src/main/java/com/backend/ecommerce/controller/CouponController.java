package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.CouponDto;
import com.backend.ecommerce.dto.CouponPreviewRequest;
import com.backend.ecommerce.dto.CouponPreviewResponse;
import com.backend.ecommerce.dto.CouponUsageDto;
import com.backend.ecommerce.service.CouponPreviewService;
import com.backend.ecommerce.service.CouponService;
import com.backend.ecommerce.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CouponPreviewService couponPreviewService;
    private final SecurityService securityService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CouponDto>> findAll() {
        return ResponseEntity.ok(couponService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CouponDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(couponService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CouponDto> create(@Valid @RequestBody CouponDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.create(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CouponDto> update(@PathVariable Integer id, @Valid @RequestBody CouponDto dto) {
        return ResponseEntity.ok(couponService.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activation")
    public ResponseEntity<CouponDto> setActivation(@PathVariable Integer id, @RequestParam boolean active) {
        return ResponseEntity.ok(couponService.setCouponActive(id, active));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/usages")
    public ResponseEntity<List<CouponUsageDto>> findUsages(@PathVariable Integer id) {
        return ResponseEntity.ok(couponService.getCouponUsages(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/preview")
    public ResponseEntity<CouponPreviewResponse> previewCoupon(@Valid @RequestBody CouponPreviewRequest request) {
        Integer userId = securityService.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(couponPreviewService.preview(userId, request));
    }
}
