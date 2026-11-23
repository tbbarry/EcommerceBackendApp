package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO complet pour l'affichage détaillé d'un produit côté admin.
 * Contient toutes les informations nécessaires pour éditer/gérer un produit.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminDetailResponse {
    private Integer id;
    private String name;
    private String description;
    private String brand;
    private String slug;
    private BigDecimal price;
    private Integer categoryId;
    private String categoryName;
    private String status; // "ACTIVE", "INACTIVE", "DRAFT"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<VariantAdminResponse> variants = new ArrayList<>();
    private List<ProductImageDto> images = new ArrayList<>();
}
