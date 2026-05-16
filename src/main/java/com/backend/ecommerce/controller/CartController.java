package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.CartDto;
import com.backend.ecommerce.dto.CartItemDto;
import com.backend.ecommerce.service.CartService;
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
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartDto>> findAll() {
        return ResponseEntity.ok(cartService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(cartService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CartDto> create(@Valid @RequestBody CartDto dto) {


        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartDto> update(@PathVariable Integer id, @Valid @RequestBody CartDto dto) {
        return ResponseEntity.ok(cartService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cartService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<CartDto> getCartByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/users/{userId}/items")
    public ResponseEntity<CartDto> addItem(
            @PathVariable Integer userId,
            @Valid @RequestBody CartItemDto request
    ) {
        return ResponseEntity.ok(
                cartService.addItemToCart(userId, request.getVariantId(), request.getQuantity())
        );
    }

    @PutMapping("/users/{userId}/items/{cartItemId}")
    public ResponseEntity<CartDto> updateQuantity(
            @PathVariable Integer userId,
            @PathVariable Integer cartItemId,
            @Valid @RequestBody CartItemDto request
    ) {
        return ResponseEntity.ok(
                cartService.updateItemQuantity(userId, cartItemId, request.getQuantity())
        );
    }

    @DeleteMapping("/users/{userId}/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Integer userId,
            @PathVariable Integer cartItemId
    ) {
        cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
