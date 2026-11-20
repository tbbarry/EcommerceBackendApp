package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariantFullResponse {
    private Integer id;
    private String sku;
    private BigDecimal price;
    
    // Attributs de la variante
    private List<VariantAttributeValueDto> attributeValues = new ArrayList<>();
    
    // Stock de la variante
    private VariantStockResponse stock;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
