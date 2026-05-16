package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    private String orderNumber;
    private LocalDateTime dateOrder;
    private BigDecimal taxAmount;
    private BigDecimal subtotal;
    private BigDecimal shippingOrder;
    private BigDecimal total;
    private String status;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    private DeliveryAddress deliveryAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Invoice invoice;
}