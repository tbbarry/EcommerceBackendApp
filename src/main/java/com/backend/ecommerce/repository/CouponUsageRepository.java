package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Integer> {

    boolean existsByCouponIdAndUserId(Integer couponId, Integer userId);

    long countByCouponId(Integer couponId);

    List<CouponUsage> findByCouponId(Integer couponId);

    List<CouponUsage> findByUserId(Integer userId);
}
