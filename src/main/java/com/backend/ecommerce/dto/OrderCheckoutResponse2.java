package com.backend.ecommerce.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderCheckoutResponse2 {

    private Integer orderId;
    private String orderNumber;
    private String paymentUrl;
}
