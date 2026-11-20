package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
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
@Data
public class ProductFullResponse {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String slug;
    private String brand;
    private Integer categoryId;
    private String categoryName;
    
    // Variantes du produit
    private List<VariantFullResponse> variants = new ArrayList<>();
    
    // Images du produit
    private List<ProductImageDto> images = new ArrayList<>();
    
    // Métadonnées
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
