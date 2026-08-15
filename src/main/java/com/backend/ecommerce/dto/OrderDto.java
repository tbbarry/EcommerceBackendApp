package com.backend.ecommerce.dto;

import com.backend.ecommerce.entity.DeliveryType;
import com.backend.ecommerce.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private BigDecimal shippingCost;
    private String shippingMethodName;
    private DeliveryType deliveryType;
    private Integer deliveryMinDays;
    private Integer deliveryMaxDays;
    private String shippingFirstName;
    private String shippingLastName;
    private String shippingStreet;
    private String shippingCity;
    private String shippingState;
    private String shippingZipCode;
    private String shippingCountry;
    private String shippingPhone;
    private String couponCodeUsed;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal taxRate;
    private List<OrderItemDto> items;
}
