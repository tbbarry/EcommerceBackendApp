package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogSearchResponse {
    private List<CatalogProductCardResponse> items = new ArrayList<>();
    private CatalogFacetsResponse facets = new CatalogFacetsResponse();
    private CatalogPaginationResponse pagination;
    private CatalogAppliedFiltersResponse appliedFilters;
    private String sort;
    private List<String> availableSorts = new ArrayList<>();
}
