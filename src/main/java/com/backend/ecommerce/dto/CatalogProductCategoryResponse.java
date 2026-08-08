package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogProductCategoryResponse {
    private Integer id;
    private String name;
    private Integer parentId;
    private String parentName;
}
