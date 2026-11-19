package com.backend.ecommerce.dto;

import java.math.BigDecimal;


public record ProductCardDto(
    Long id,
    String name,
    String slug,
    BigDecimal price,
    String imageUrl,
    String categoryName,
    Long categoryId,
    BigDecimal basePriceMin,
    BigDecimal basePriceMax,
    BigDecimal finalPriceMin,
    BigDecimal finalPriceMax,
    BigDecimal displayPrice,
    Boolean hasDiscount,
    String promoScope,
    BigDecimal discountPercentGlobal,
    BigDecimal discountPercentMin,
    BigDecimal discountPercentMax
) {}
