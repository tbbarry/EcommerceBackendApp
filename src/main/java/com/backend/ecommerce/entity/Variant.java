package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "variants",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_variant_product_color_size", columnNames = {"productId", "color", "size"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Variant extends BaseEntity {

    @Column
    private String color;

    @Column
    private String size;

    @Column(nullable = false, unique = true)
    private String sku;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stock;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Valid
    @Builder.Default
    @OneToOne(mappedBy = "variant", cascade = CascadeType.ALL)
    private Stock stockRef = null;

    @Valid
    @Builder.Default
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    @Valid
    @Builder.Default
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Valid
    @Builder.Default
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
}
