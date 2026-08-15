    
package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock extends BaseEntity {

    @Builder.Default
    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer quantity = 0;

    @Builder.Default
    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variantId", nullable = false)
    private Variant variant;

    @Transient
    public Integer getAvailableQuantity() {
        return Math.max(quantity - reservedQuantity, 0);
    }
}