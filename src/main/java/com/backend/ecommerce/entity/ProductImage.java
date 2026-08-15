package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String url;

    @Column(nullable = true)
    private String objectKey;

    @NotBlank
    @Column(nullable = false)
    private String alt;

    @Builder.Default
    @Column(nullable = false)
    private boolean isMain = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer displayOrder = 0;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productColorId")
    private ProductColor productColor;
}
