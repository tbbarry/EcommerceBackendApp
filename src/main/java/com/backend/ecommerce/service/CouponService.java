package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CouponDto;
import com.backend.ecommerce.dto.CouponUsageDto;
import com.backend.ecommerce.entity.Coupon;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService extends CrudService<CouponDto, Integer> {

    Coupon validateCoupon(String code, User user, BigDecimal orderAmount);

    void registerUsage(Coupon coupon, User user, Order order);

    List<CouponUsageDto> getCouponUsages(Integer couponId);

    CouponDto setCouponActive(Integer couponId, boolean active);
}
