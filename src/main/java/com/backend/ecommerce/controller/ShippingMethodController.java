package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ShippingMethodDto;
import com.backend.ecommerce.service.ShippingMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-methods")
@RequiredArgsConstructor
public class ShippingMethodController {

    private final ShippingMethodService shippingMethodService;

    @GetMapping
    public ResponseEntity<List<ShippingMethodDto>> findActiveShippingMethods() {
        return ResponseEntity.ok(shippingMethodService.findActiveMethods());
    }
}
