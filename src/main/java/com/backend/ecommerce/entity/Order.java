package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String orderNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateOrder;

    @NotNull
    @Column(nullable = false)
    private BigDecimal taxAmount;

    @NotNull
    @Column(nullable = false)
    private BigDecimal subtotal;

    @NotNull
    @Column(nullable = false)
    private BigDecimal shippingOrder;

    @NotNull
    @Column(nullable = false)
    private BigDecimal total;

    @NotBlank
    @Column(nullable = false)
    private String status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deliveryAddressId", nullable = false)
    private DeliveryAddress deliveryAddress;

    @Valid
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Valid
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @Valid
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Invoice invoice;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal taxRate;
}