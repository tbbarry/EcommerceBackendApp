package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDiscountResult {

    private BigDecimal discountOnSubtotal;
    private BigDecimal discountOnShipping;
    private BigDecimal totalDiscount;
}
