package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVariantCreateRequest {
    private String sku;
    private BigDecimal price;
    private List<Integer> attributeValueIds;
    private Integer quantity;
}
