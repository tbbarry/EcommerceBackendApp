package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.CartItemDto;
import com.backend.ecommerce.service.CartItemService;
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
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<List<CartItemDto>> findAll() {
        return ResponseEntity.ok(cartItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartItemDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(cartItemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CartItemDto> create(@Valid @RequestBody CartItemDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemDto> update(@PathVariable Integer id, @Valid @RequestBody CartItemDto dto) {
        return ResponseEntity.ok(cartItemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cartItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

