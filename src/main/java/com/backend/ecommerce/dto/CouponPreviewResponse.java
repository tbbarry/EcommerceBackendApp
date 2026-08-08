package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponPreviewResponse {

    private String couponCode;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal taxRate;
    private BigDecimal discountOnSubtotal;
    private BigDecimal discountOnShipping;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal total;
}
