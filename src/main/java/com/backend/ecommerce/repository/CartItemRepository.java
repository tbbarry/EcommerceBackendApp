package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartIdAndVariantId(Integer cartId, Integer variantId);

    List<CartItem> findByCartId(Integer cartId);
    void deleteByCartId(Integer cartId);
}
