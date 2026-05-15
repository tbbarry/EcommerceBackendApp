package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Integer id;
    private String paymentMethod;
    private Integer orderId;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
