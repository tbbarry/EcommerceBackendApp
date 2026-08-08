package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CouponDiscountResult;
import com.backend.ecommerce.entity.Coupon;
import com.backend.ecommerce.entity.DiscountType;
import com.backend.ecommerce.service.CouponCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CouponCalculationServiceImpl implements CouponCalculationService {

    @Override
    public CouponDiscountResult calculateDiscount(Coupon coupon, BigDecimal subtotal, BigDecimal shippingCost) {
        BigDecimal safeSubtotal = subtotal != null ? subtotal : BigDecimal.ZERO;
        BigDecimal safeShipping = shippingCost != null ? shippingCost : BigDecimal.ZERO;

        BigDecimal discountOnSubtotal = BigDecimal.ZERO;
        BigDecimal discountOnShipping = BigDecimal.ZERO;

        if (coupon != null) {
            if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                BigDecimal raw = safeSubtotal
                        .multiply(coupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                discountOnSubtotal = raw.min(safeSubtotal);
            } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                discountOnSubtotal = coupon.getDiscountValue().min(safeSubtotal).setScale(2, RoundingMode.HALF_UP);
            } else if (coupon.getDiscountType() == DiscountType.FREE_SHIPPING) {
                discountOnShipping = safeShipping.setScale(2, RoundingMode.HALF_UP);
            }
        }

        BigDecimal totalDiscount = discountOnSubtotal.add(discountOnShipping).setScale(2, RoundingMode.HALF_UP);

        return CouponDiscountResult.builder()
                .discountOnSubtotal(discountOnSubtotal.setScale(2, RoundingMode.HALF_UP))
                .discountOnShipping(discountOnShipping.setScale(2, RoundingMode.HALF_UP))
                .totalDiscount(totalDiscount)
                .build();
    }
}
