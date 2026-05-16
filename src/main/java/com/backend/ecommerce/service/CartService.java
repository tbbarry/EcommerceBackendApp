package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CartDto;

public interface CartService extends CrudService<CartDto, Integer> {
    CartDto getCartByUserId(Integer userId);

    CartDto addItemToCart(Integer userId, Integer variantId, Integer quantity);

    CartDto updateItemQuantity(Integer userId, Integer cartItemId, Integer quantity);

    void removeItemFromCart(Integer userId, Integer cartItemId);

    void clearCart(Integer userId);
}
