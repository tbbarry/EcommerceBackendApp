package com.backend.ecommerce.dto;

import com.backend.ecommerce.entity.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingMethodDto {

    private Integer id;
    private String name;
    private String code;
    private DeliveryType deliveryType;
    private String description;
    private Integer minDeliveryDays;
    private Integer maxDeliveryDays;
    private BigDecimal price;
    private BigDecimal freeShippingThreshold;
    private boolean active;
}
