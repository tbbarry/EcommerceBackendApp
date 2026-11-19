package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "attributes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_attribute_name",
            columnNames = "name"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attribute extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(
        mappedBy = "attribute",
        cascade = CascadeType.REMOVE
    )
    private List<AttributeValue> values = new ArrayList<>();
}