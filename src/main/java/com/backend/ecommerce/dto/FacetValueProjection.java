package com.backend.ecommerce.dto;

public record FacetValueProjection(

    Long facetId,
    String facetCode,
    Long valueId,
    String valueLabel

) {}