package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Variant extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String color;

    @NotBlank
    @Column(nullable = false)
    private String size;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String sku;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Valid
    @NotNull
    @OneToOne(mappedBy = "variant", cascade = CascadeType.ALL)
    private Stock stock;

    @Valid
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    @Valid
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Valid
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
}
