package com.backend.ecommerce.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock extends BaseEntity {

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer quantity;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variantId", nullable = false)
    private Variant variant;
}