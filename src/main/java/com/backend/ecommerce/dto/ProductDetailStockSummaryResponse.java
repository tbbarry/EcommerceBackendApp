package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailStockSummaryResponse {
    private int totalStock;
    private int inStockVariants;
    private int outOfStockVariants;
    private boolean hasAnyStock;
}
