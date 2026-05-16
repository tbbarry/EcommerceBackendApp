package com.backend.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Integer id;
    private Integer cartId;
    @NotNull
    private Integer variantId;
    @Min(1)
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
