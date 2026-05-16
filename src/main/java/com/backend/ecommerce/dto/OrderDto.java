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
public class OrderDto {
    private Integer id;
    private Integer userId;
    private Integer deliveryAddressId;
    private String orderNumber;
    private LocalDateTime dateOrder;
    private BigDecimal taxAmount;
    private BigDecimal subtotal;
    private BigDecimal shippingOrder;
    private BigDecimal total;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
