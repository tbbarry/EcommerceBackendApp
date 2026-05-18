package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity{

    @NotBlank
    @Column(nullable = false)
    private String paymentMethod;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    @Column(nullable = false)
    private BigDecimal amount;

    //A voir si peut être null ou pas
    private LocalDateTime paidAt;

    @NotBlank
    @Column(nullable = false)
    private String status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;
}