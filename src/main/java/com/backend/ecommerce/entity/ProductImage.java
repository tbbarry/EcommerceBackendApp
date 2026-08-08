package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "product_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String url;

    @NotBlank
    @Column(nullable = false)
    private String alt;

    @Column(nullable = false)
    private boolean isMain;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productColorId")
    private ProductColor productColor;
}
