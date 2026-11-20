package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO léger pour l'affichage d'une liste de produits côté admin.
 * Utilisé dans les tableaux de bord pour faciliter la gestion des produits.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminListResponse {
    private Integer id;
    private String name;
    private String brand;
    private String categoryName;
    private BigDecimal price;
    private Integer totalVariants;
    private Integer totalStock;
    private String status; // "ACTIVE", "INACTIVE", "DRAFT"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
