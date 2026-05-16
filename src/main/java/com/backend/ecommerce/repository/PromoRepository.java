package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PromoRepository extends JpaRepository<Promo, Integer> {
    Optional<Promo> findByCode(String code);
    boolean existsByCode(String code);
}
