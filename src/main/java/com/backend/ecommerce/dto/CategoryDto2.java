package com.backend.ecommerce.dto;

import java.util.List;

public record CategoryDto2(
    Long id,
    String name,
    List<CategoryDto2> children
) {}