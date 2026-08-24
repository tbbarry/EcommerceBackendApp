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

    @Column
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

    /**
     * Produit auquel appartient l'image.
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
     * Valeur d'attribut visuel qui pilote l'image.
     *
     * Exemple :
     *
     * Attribute : Couleur
     * Value     : Rouge
     *
     * Si null, l'image est une image générale du produit.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visualAttributeValueId")
    private AttributeValue visualAttributeValue;
}