package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProductId(Integer productId);
    List<ProductImage> findByProductIdIn(List<Integer> productIds);
    Optional<ProductImage> findByProductIdAndIsMainTrue(Integer productId);
}
