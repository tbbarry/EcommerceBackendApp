package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends BaseEntity {

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateInvoice;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal total;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
