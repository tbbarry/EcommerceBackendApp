package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogProductVariantDetailResponse {
    private Integer id;
    private String sku;
    private String color;
    private String size;
    private BigDecimal price;
    private int stock;
    private boolean inStock;
    private List<Integer> imageIds = new ArrayList<>();
}
