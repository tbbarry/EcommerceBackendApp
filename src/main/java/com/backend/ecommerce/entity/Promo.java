package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promo extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String code;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal discountValue;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endDate;

    @NotNull
    @Column(nullable = false)
    private Boolean isActive;

    @NotEmpty
    @Valid
    @OneToMany(mappedBy = "promo", cascade = CascadeType.ALL)
    private List<ProductPromo> productPromos = new ArrayList<>();
}