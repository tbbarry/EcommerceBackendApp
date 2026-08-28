package com.backend.ecommerce.dto;

import java.math.BigDecimal;

public record ProductCardDto(
    Long id,
    String name,
    String slug,
    BigDecimal price,
    String imageUrl,
    String categoryName 
) {}
