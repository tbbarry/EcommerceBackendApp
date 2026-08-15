package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface ProductColorRepository extends JpaRepository<ProductColor, Integer> {
    List<ProductColor> findByProductId(Integer productId);
    Optional<ProductColor> findByProductIdAndNameIgnoreCase(
        Integer productId,
        String name
    );
}
