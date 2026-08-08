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
    private BigDecimal shippingCost;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String shippingMethodName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeliveryType deliveryType;

    @NotNull
    @Column(nullable = false)
    private Integer deliveryMinDays;

    @NotNull
    @Column(nullable = false)
    private Integer deliveryMaxDays;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String shippingFirstName;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String shippingLastName;

    @NotBlank
    @Column(nullable = false)
    private String shippingStreet;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String shippingCity;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String shippingState;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String shippingZipCode;

    @NotBlank
    @Column(nullable = false, length = 2)
    private String shippingCountry;

    @NotBlank
    @Column(nullable = false, length = 40)
    private String shippingPhone;

    @Column(length = 60)
    private String couponCodeUsed;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal discountAmount;

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
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Valid
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @Valid
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Invoice invoice;

    @Valid
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private DeliveryPreference deliveryPreference;

    @Valid
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private CouponUsage couponUsage;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal taxRate;
}