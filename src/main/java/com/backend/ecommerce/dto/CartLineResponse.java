package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartLineResponse {
    private Integer cartItemId;
    private Integer variantId;
    private String sku;
    private Integer productId;
    private String productName;
    private String productSlug;
    private String brand;
    private String color;
    private String size;
    private Integer quantity;
    private Integer availableStock;
    private boolean inStock;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String imageUrl;
    private String imageAlt;
}
