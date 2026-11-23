package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CartDto;
import com.backend.ecommerce.dto.CartItemUpsertRequest;
import com.backend.ecommerce.dto.CartViewResponse;
import com.backend.ecommerce.entity.Order;

import java.util.List;

public interface CartService extends CrudService<CartDto, Integer> {
    CartDto getCartByUserId(Integer userId);

    CartDto addItemToCart(Integer userId, Integer variantId, Integer quantity);

    CartDto updateItemQuantity(Integer userId, Integer cartItemId, Integer quantity);

    void removeItemFromCart(Integer userId, Integer cartItemId);

    void clearCart(Integer userId);

    CartViewResponse getCartViewByUserId(Integer userId);

    CartViewResponse addItemToCartView(Integer userId, Integer variantId, Integer quantity);

    CartViewResponse updateItemQuantityView(Integer userId, Integer cartItemId, Integer quantity);

    CartViewResponse removeItemFromCartView(Integer userId, Integer cartItemId);

    CartViewResponse clearCartView(Integer userId);
    void removePurchasedItems(Order order);

    CartViewResponse syncCart(Integer userId, List<CartItemUpsertRequest> items, boolean replaceExisting);
}
