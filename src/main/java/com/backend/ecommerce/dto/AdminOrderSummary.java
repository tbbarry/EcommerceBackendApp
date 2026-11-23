package com.backend.ecommerce.dto;

import com.backend.ecommerce.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminOrderSummary(
        Integer id, String orderNumber, LocalDateTime dateOrder, String customerName,
        String customerEmail, BigDecimal total, OrderStatus status) {
}
