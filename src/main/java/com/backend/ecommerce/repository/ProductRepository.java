package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
