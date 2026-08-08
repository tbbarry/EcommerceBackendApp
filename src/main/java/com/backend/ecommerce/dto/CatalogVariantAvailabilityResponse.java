package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogVariantAvailabilityResponse {
    private Integer variantId;
    private int stock;
    private boolean inStock;
}
