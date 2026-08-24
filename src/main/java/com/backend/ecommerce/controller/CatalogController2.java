package com.backend.ecommerce.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import com.backend.ecommerce.service.CatalogService2;
import com.backend.ecommerce.dto.CatalogResponse;
import com.backend.ecommerce.dto.CatalogSearchRequest;

import com.backend.ecommerce.dto.FacetFilter;

@RestController
@RequestMapping("/api/catalog/products")
@RequiredArgsConstructor
public class CatalogController2 {

    private final CatalogService2 catalogService;

@GetMapping("/products")
public CatalogResponse search(
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "24") Integer size,
        @RequestParam(required = false) String query,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String facets
) {

    CatalogSearchRequest request = new CatalogSearchRequest();

    request.setPage(page);
    request.setSize(size);
    request.setQuery(query);
    request.setMinPrice(minPrice);
    request.setMaxPrice(maxPrice);

    if (categoryId != null) {
        request.setCategoryId(categoryId);
    }

    request.setFacets(parseFacets(facets));

    return catalogService.search(request);
}

private List<FacetFilter> parseFacets(String facets) {

    if (facets == null || facets.isBlank()) {
        return new ArrayList<>();
    }

    return Arrays.stream(facets.split(";"))
            .map(String::trim)
            .filter(facet -> !facet.isEmpty())
            .map(this::parseFacet)
            .toList();
}

private FacetFilter parseFacet(String facet) {

    String[] parts = facet.split(":", 2);

    if (parts.length != 2) {
        throw new IllegalArgumentException(
                "Format de facette invalide : " + facet
        );
    }

    Long facetId;

    try {
        facetId = Long.valueOf(parts[0].trim());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
                "ID de facette invalide : " + parts[0]
        );
    }

    List<Long> values = Arrays.stream(parts[1].split(","))
            .map(String::trim)
            .filter(value -> !value.isEmpty())
            .map(value -> {
                try {
                    return Long.valueOf(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "ID de valeur de facette invalide : " + value
                    );
                }
            })
            .toList();

    if (values.isEmpty()) {
        throw new IllegalArgumentException(
                "Aucune valeur fournie pour la facette : " + facetId
        );
    }

    return new FacetFilter(facetId, values);
}

}