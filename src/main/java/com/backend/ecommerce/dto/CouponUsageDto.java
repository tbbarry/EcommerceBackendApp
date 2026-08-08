package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsageDto {

    private Integer id;
    private Integer couponId;
    private String couponCode;
    private Integer userId;
    private Integer orderId;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;
}
