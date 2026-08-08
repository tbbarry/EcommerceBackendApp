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
public class CatalogFacetsResponse {
    private List<CatalogFacetItemResponse> categories = new ArrayList<>();
    private List<CatalogCategoryTreeItemResponse> categoryTree = new ArrayList<>();
    private List<CatalogFacetItemResponse> colors = new ArrayList<>();
    private List<CatalogFacetItemResponse> sizes = new ArrayList<>();
}
