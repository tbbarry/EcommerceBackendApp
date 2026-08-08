package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CouponDiscountResult;
import com.backend.ecommerce.entity.Coupon;

import java.math.BigDecimal;

public interface CouponCalculationService {

    CouponDiscountResult calculateDiscount(Coupon coupon, BigDecimal subtotal, BigDecimal shippingCost);
}
