package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String slug;
    private String brand;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
