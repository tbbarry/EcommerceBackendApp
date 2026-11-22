package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.AdminOrderDetail;
import com.backend.ecommerce.dto.AdminOrderStatusRequest;
import com.backend.ecommerce.dto.AdminOrderSummary;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.service.impl.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final AdminOrderService service;

    @GetMapping
    public Page<AdminOrderSummary> list(@RequestParam(defaultValue = "") String search,
                                        @RequestParam(required = false) OrderStatus status,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return service.list(search, status, page, size);
    }

    @GetMapping("/{id}")
    public AdminOrderDetail detail(@PathVariable Integer id) {
        return service.detail(id);
    }

    @PatchMapping("/{id}/status")
    public AdminOrderDetail advance(@PathVariable Integer id, @Valid @RequestBody AdminOrderStatusRequest request) {
        return service.advance(id, request);
    }
}
