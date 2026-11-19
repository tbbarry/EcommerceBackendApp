package com.backend.ecommerce.dto;
public record FacetCountProjection(
        long count,
        Long productId,
        Long facetId,
        Long facetValueId
) {}