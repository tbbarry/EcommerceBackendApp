package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant, Integer> {
    Optional<Variant> findBySku(String sku);
    boolean existsBySku(String sku);
}
