package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCheckoutResponse {

    private Integer orderId;
    private String orderNumber;
    private LocalDateTime dateOrder;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String couponCodeUsed;
    private BigDecimal discountAmount;
    private String shippingMethodName;
    private Integer deliveryMinDays;
    private Integer deliveryMaxDays;
}
