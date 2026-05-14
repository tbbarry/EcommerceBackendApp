package com.example.ecommerce.DT0;
import lombok.Data;

@Data
public class ProductRequestDTO {
    private String name;
    private String description;
    private Long categoryId;
}
