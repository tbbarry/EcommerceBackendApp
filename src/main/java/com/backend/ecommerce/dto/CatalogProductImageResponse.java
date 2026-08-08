package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogProductImageResponse {
    private Integer id;
    private String url;
    private String alt;
    private boolean isMain;
    private String color;
}
