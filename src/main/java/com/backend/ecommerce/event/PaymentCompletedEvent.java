package com.backend.ecommerce.event;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import lombok.*;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {

    private Integer orderId;

    private Integer userId;

    private String paymentIntentId;

    private BigDecimal amount;
}