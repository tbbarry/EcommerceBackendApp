package com.backend.ecommerce.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "product_colors", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_color_name", columnNames = {"productId", "name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductColor extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;
    @Column(nullable = true)
    private String hexCode;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Valid
    @Builder.Default
    @OneToMany(
        mappedBy = "productColor",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ProductImage> images = new ArrayList<>();

    @Valid
    @Builder.Default
    @OneToMany(
        mappedBy = "productColor",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Variant> variants = new ArrayList<>();


}
