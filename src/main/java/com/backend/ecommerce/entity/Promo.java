package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "promos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promo extends BaseEntity {

    private String code;
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "promo", cascade = CascadeType.ALL)
    private List<ProductPromo> productPromos;
}