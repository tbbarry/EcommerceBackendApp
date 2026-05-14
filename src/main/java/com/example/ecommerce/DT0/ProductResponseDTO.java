package com.example.ecommerce.DT0;

import java.util.List;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private List<Long> variantIds;
    private List<Long> imageIds;
}
