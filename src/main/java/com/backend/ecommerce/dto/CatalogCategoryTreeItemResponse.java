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
public class CatalogCategoryTreeItemResponse {
    private String value;
    private long count;
    private List<CatalogCategoryTreeItemResponse> subCategories = new ArrayList<>();
}
