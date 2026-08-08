package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.CartDto;
import com.backend.ecommerce.dto.CartItemQuantityUpdateRequest;
import com.backend.ecommerce.dto.CartItemUpsertRequest;
import com.backend.ecommerce.dto.CartSyncRequest;
import com.backend.ecommerce.dto.CartViewResponse;
import com.backend.ecommerce.service.SecurityService;
import com.backend.ecommerce.service.CartService;
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
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final SecurityService securityService;

    // ADMIN uniquement : voir tous les paniers
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CartDto>> findAll() {
        return ResponseEntity.ok(cartService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CartDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(cartService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CartDto> create(@Valid @RequestBody CartDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.create(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CartDto> update(@PathVariable Integer id, @Valid @RequestBody CartDto dto) {
        return ResponseEntity.ok(cartService.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cartService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
    @GetMapping("/users/{userId}")
    public ResponseEntity<CartViewResponse> getCartByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.getCartViewByUserId(userId));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
    @PostMapping("/users/{userId}/items")
        public ResponseEntity<CartViewResponse> addItem(
            @PathVariable Integer userId,
            @Valid @RequestBody CartItemUpsertRequest request
    ) {
        return ResponseEntity.ok(
            cartService.addItemToCartView(userId, request.getVariantId(), request.getQuantity())
        );
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
    @PutMapping("/users/{userId}/items/{cartItemId}")
        public ResponseEntity<CartViewResponse> updateQuantity(
            @PathVariable Integer userId,
            @PathVariable Integer cartItemId,
            @Valid @RequestBody CartItemQuantityUpdateRequest request
    ) {
        return ResponseEntity.ok(
            cartService.updateItemQuantityView(userId, cartItemId, request.getQuantity())
        );
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
    @DeleteMapping("/users/{userId}/items/{cartItemId}")
    public ResponseEntity<CartViewResponse> removeItem(
            @PathVariable Integer userId,
            @PathVariable Integer cartItemId
    ) {
        return ResponseEntity.ok(cartService.removeItemFromCartView(userId, cartItemId));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
    @DeleteMapping("/users/{userId}/clear")
    public ResponseEntity<CartViewResponse> clearCart(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.clearCartView(userId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<CartViewResponse> myCart() {
        Integer userId = securityService.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(cartService.getCartViewByUserId(userId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/items")
    public ResponseEntity<CartViewResponse> addMyItem(@Valid @RequestBody CartItemUpsertRequest request) {
        Integer userId = securityService.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(cartService.addItemToCartView(userId, request.getVariantId(), request.getQuantity()));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/items/{cartItemId}")
    public ResponseEntity<CartViewResponse> updateMyItem(
            @PathVariable Integer cartItemId,
            @Valid @RequestBody CartItemQuantityUpdateRequest request
    ) {
        Integer userId = securityService.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(cartService.updateItemQuantityView(userId, cartItemId, request.getQuantity()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me/items/{cartItemId}")
    public ResponseEntity<CartViewResponse> removeMyItem(@PathVariable Integer cartItemId) {
        Integer userId = securityService.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(cartService.removeItemFromCartView(userId, cartItemId));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<CartViewResponse> clearMyCart() {
        Integer userId = securityService.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(cartService.clearCartView(userId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/sync")
    public ResponseEntity<CartViewResponse> syncMyCart(@Valid @RequestBody CartSyncRequest request) {
        Integer userId = securityService.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(cartService.syncCart(userId, request.getItems(), request.isReplaceExisting()));
    }
}
