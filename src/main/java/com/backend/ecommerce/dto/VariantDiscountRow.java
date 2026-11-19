package com.backend.ecommerce.dto;

import java.math.BigDecimal;

public record VariantDiscountRow(
        Integer variantId,
        BigDecimal discountPercent
) {
}
