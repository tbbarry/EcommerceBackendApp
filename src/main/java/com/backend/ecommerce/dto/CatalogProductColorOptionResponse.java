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
public class CatalogProductColorOptionResponse {
    private String name;
    private boolean available;
    private int totalStock;
    private int imageCount;
    private List<String> sizesAvailable = new ArrayList<>();
}
