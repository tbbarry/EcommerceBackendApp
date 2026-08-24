package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
    name = "variant_attribute_values",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_variant_attribute_value",
            columnNames = {
                "variantId",
                "attributeValueId"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantAttributeValue extends BaseEntity {

    @NotNull
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "variantId",
        nullable = false
    )
    private Variant variant;

    @NotNull
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "attributeValueId",
        nullable = false
    )
    private AttributeValue attributeValue;
}