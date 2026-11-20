package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminImageCreateRequest {
    private String url;
    private String alt;
    private boolean isMain;
    private Integer displayOrder;
    private Integer visualAttributeValueId;
}
