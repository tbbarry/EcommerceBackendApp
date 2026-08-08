package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariantResponse {

    private Integer id;
    private String sku;
    private Integer stock;
    private String size;
    private String color;
}
