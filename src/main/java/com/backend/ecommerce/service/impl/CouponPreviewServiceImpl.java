package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CartViewResponse;
import com.backend.ecommerce.dto.CouponDiscountResult;
import com.backend.ecommerce.dto.CouponPreviewRequest;
import com.backend.ecommerce.dto.CouponPreviewResponse;
import com.backend.ecommerce.entity.Coupon;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ShippingMethodRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.service.CartService;
import com.backend.ecommerce.service.CouponCalculationService;
import com.backend.ecommerce.service.CouponPreviewService;
import com.backend.ecommerce.service.CouponService;
import com.backend.ecommerce.service.SecurityService;
import com.backend.ecommerce.service.ShippingCalculationService;
import com.backend.ecommerce.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponPreviewServiceImpl implements CouponPreviewService {

    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final CartService cartService;
    private final CouponService couponService;
    private final CouponCalculationService couponCalculationService;
    private final ShippingCalculationService shippingCalculationService;
    private final TaxService taxService;

    @Override
    public CouponPreviewResponse preview(Integer userId, CouponPreviewRequest request) {
        Integer resolvedUserId = userId != null ? userId : securityService.getCurrentUserIdOrThrow();

        User user = userRepository.findById(resolvedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + resolvedUserId));

        CartViewResponse cart = cartService.getCartViewByUserId(resolvedUserId);
        BigDecimal subtotal = cart.getSubtotal() != null ? cart.getSubtotal() : BigDecimal.ZERO;

        BigDecimal shippingCost = BigDecimal.ZERO;
        if (request.getShippingMethodId() != null) {
            ShippingMethod shippingMethod = shippingMethodRepository.findByIdAndActiveTrue(request.getShippingMethodId())
                    .orElseThrow(() -> new ResourceNotFoundException("ShippingMethod not found with id: " + request.getShippingMethodId()));
            shippingCost = shippingCalculationService.calculateShippingCost(subtotal, shippingMethod)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        Coupon coupon = couponService.validateCoupon(request.getCouponCode(), user, subtotal);
        CouponDiscountResult discountResult = couponCalculationService.calculateDiscount(coupon, subtotal, shippingCost);

        BigDecimal discountedSubtotal = subtotal.subtract(discountResult.getDiscountOnSubtotal())
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountedShipping = shippingCost.subtract(discountResult.getDiscountOnShipping())
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxRate = taxService.getCurrentTaxRate();
        BigDecimal taxAmount = discountedSubtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = discountedSubtotal.add(discountedShipping).add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        return CouponPreviewResponse.builder()
                .couponCode(coupon.getCode())
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .shippingCost(shippingCost)
                .taxRate(taxRate)
                .discountOnSubtotal(discountResult.getDiscountOnSubtotal())
                .discountOnShipping(discountResult.getDiscountOnShipping())
                .discountAmount(discountResult.getTotalDiscount())
                .taxAmount(taxAmount)
                .total(total)
                .build();
    }
}
