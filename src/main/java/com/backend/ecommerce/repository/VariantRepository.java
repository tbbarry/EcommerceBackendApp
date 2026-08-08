package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant, Integer> {
    Optional<Variant> findBySku(String sku);
    boolean existsBySku(String sku);
    boolean existsByProductIdAndColorIgnoreCaseAndSizeIgnoreCase(Integer productId, String color, String size);
    List<Variant> findByProductId(Integer productId);
    List<Variant> findByProductIdIn(List<Integer> productIds);
}
