package com.backend.ecommerce.dto;

import java.util.List;

public record CatalogResponse(
    List<ProductCardDto> products,
    List<CategoryDto2> categories,
    List<FacetDto> facets,
    long totalProducts,
    int page,
    int size
) {}