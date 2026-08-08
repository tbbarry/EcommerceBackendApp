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
public class ProductResponse {

    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String brand;
    private String slug;

    private List<String> colors = new ArrayList<>();
    private List<ProductImageResponse> images = new ArrayList<>();
    private List<VariantResponse> variants = new ArrayList<>();
}
