package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogAppliedFiltersResponse {
    private String query;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private List<String> categories = new ArrayList<>();
    private List<String> colors = new ArrayList<>();
    private List<String> sizes = new ArrayList<>();
}
