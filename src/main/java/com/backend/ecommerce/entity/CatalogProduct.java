package com.backend.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "catalog_product")
public class CatalogProduct extends BaseEntity {

    
    private Long productId;

    private String name;

    // Backward-compatible field for legacy consumers.
    private BigDecimal price;

    private BigDecimal basePriceMin;

    private BigDecimal basePriceMax;

    private BigDecimal finalPriceMin;

    private BigDecimal finalPriceMax;

    private BigDecimal displayPrice;

    private BigDecimal discountPercentGlobal;

    private BigDecimal discountPercentMin;

    private BigDecimal discountPercentMax;

    private Boolean hasDiscount;

    private String promoScope;

    private String slug;

    private String imageUrl;
    private String brand;
    private String categoryName;
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'ACTIVE'")
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;
}