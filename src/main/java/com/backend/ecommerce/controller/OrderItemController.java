package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.OrderItemDto;
import com.backend.ecommerce.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<List<OrderItemDto>> findAll() {
        return ResponseEntity.ok(orderItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderItemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> create(@Valid @RequestBody OrderItemDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> update(@PathVariable Integer id, @Valid @RequestBody OrderItemDto dto) {
        return ResponseEntity.ok(orderItemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        orderItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

