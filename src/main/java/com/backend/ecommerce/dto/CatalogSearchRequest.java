package com.backend.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CatalogSearchRequest {

    private Integer page = 0;

    private Integer size = 24;

    private String query;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    /**
     * Catégorie mère ou sous-catégorie sélectionnée.
     * Une seule catégorie possible.
     */
    private Long categoryId;


    /**
     * Facettes dynamiques :
     * color:1;size:6;brand:nike
     */
    private List<FacetFilter> facets = new ArrayList<>();
}