package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDto {
    private Integer id;
    private Integer productId;
    private Integer categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
