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

    @Column(nullable = false, unique = true)
    private String sku;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Produit parent.
     */
    @NotNull
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "productId",
        nullable = false
    )
    private Product product;

    /**
     * Attributs de la variante.
     *
     * Exemple :
     *
     * Taille  = M
     * Couleur = Rouge
     * Matière = Coton
     */
    @Valid
    @Builder.Default
    @OneToMany(
        mappedBy = "variant",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<VariantAttributeValue> attributeValues = new ArrayList<>();

    /**
     * Stock de la variante.
     */
    @Valid
    @Builder.Default
    @OneToOne(
        mappedBy = "variant",
        cascade = CascadeType.ALL
    )
    private Stock stock = new Stock();

    /**
     * Articles présents dans les paniers.
     */
    @Valid
    @Builder.Default
    @OneToMany(
        mappedBy = "variant",
        cascade = CascadeType.ALL
    )
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * Articles des commandes.
     */
    @Valid
    @Builder.Default
    @OneToMany(
        mappedBy = "variant",
        cascade = CascadeType.ALL
    )
    private List<OrderItem> orderItems = new ArrayList<>();
}