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
public class OrderItemDto {
    private Integer id;
    private Integer orderId;
    private Integer variantId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private BigDecimal unitprice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
