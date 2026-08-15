package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "variants"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Variant extends BaseEntity {


    @Column(nullable = true)
    private String size;

    @Column(nullable = false, unique = true)
    private String sku;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Toujours présent.
    */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    /**
     * Optionnel.
     * Null pour les produits sans couleur.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productColorId")
    private ProductColor productColor;

    @Valid
    @Builder.Default
    @OneToOne(
        mappedBy = "variant",
        cascade = CascadeType.ALL
    )
    private Stock stock = null;

    @Valid
    @Builder.Default
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    @Valid
    @Builder.Default
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
}