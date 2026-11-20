package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
    private Integer id;
    private String url;
    private String alt;
    private boolean main;  // Changed from isMain to main for API response
    private Integer displayOrder;
    private Integer visualAttributeValueId;
    private String visualAttributeValueName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
