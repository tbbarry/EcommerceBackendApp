package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour afficher les variantes côté admin avec détails de stock.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantAdminResponse {
    private Integer id;
    private String sku;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private VariantStockResponse stock;
    private List<VariantAttributeValueDto> attributeValues = new ArrayList<>();
}
