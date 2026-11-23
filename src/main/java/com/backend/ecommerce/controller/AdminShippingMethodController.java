package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ShippingMethodDto;
import com.backend.ecommerce.dto.ShippingMethodRequest;
import com.backend.ecommerce.service.ShippingMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/shipping-methods")
@RequiredArgsConstructor
public class AdminShippingMethodController {
    private final ShippingMethodService shippingMethodService;

    @GetMapping
    public List<ShippingMethodDto> list() {
        return shippingMethodService.findAllMethods();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShippingMethodDto create(@Valid @RequestBody ShippingMethodRequest request) {
        return shippingMethodService.createMethod(request);
    }

    @PutMapping("/{id}")
    public ShippingMethodDto update(@PathVariable Integer id, @Valid @RequestBody ShippingMethodRequest request) {
        return shippingMethodService.updateMethod(id, request);
    }
}
