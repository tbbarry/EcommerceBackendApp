package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Variant extends BaseEntity {

    private String color;
    private String size;

    @Column(unique = true)
    private String sku;

    private BigDecimal price;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;

    @OneToOne(mappedBy = "variant", cascade = CascadeType.ALL)
    private Stock stock;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;
}
