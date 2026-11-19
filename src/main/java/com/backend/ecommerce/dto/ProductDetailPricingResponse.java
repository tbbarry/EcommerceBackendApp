package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailPricingResponse {
    private BigDecimal basePriceMin;
    private BigDecimal basePriceMax;
    private BigDecimal finalPriceMin;
    private BigDecimal finalPriceMax;
    private BigDecimal displayPrice;
    private boolean hasDiscount;
    private String promoScope;
    private BigDecimal discountPercentGlobal;
    private BigDecimal discountPercentMin;
    private BigDecimal discountPercentMax;
}
