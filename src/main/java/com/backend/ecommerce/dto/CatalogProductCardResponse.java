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
public class CatalogProductCardResponse {
    private Integer id;
    private String name;
    private String slug;
    private String brand;
    private BigDecimal price;
    private String defaultImageUrl;
    private String defaultImageAlt;
    private List<String> categories = new ArrayList<>();
    private List<String> availableColors = new ArrayList<>();
    private List<String> availableSizes = new ArrayList<>();
    private boolean inStock;
}
