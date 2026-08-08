package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartViewResponse {
    private Integer cartId;
    private Integer userId;
    private List<CartLineResponse> items = new ArrayList<>();
    private Integer totalItems;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private boolean empty;
    private LocalDateTime updatedAt;
}
