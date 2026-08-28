package com.backend.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Entity
@Builder
@Getter
@Setter
@Table(name = "catalog_product")
public class CatalogProduct extends BaseEntity {

    
    private Long productId;

    private String name;

    private BigDecimal price;

    private String slug;

    private String imageUrl;
    private String brand;
    private String categoryName;
    private Long categoryId;
}