package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    Optional<Coupon> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}
