package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
    name = "attribute_values",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_attribute_value",
            columnNames = {"attributeId", "value"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValue extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String value;

    @NotNull
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "attributeId",
        nullable = false
    )
    private Attribute attribute;
}