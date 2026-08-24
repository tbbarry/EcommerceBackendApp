package com.backend.ecommerce.dto;

import java.util.List;
public record FacetDto(

    Long id,

    String code,

    List<FacetValueDto> values

) {}