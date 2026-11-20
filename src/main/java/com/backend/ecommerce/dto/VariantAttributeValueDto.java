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
public class VariantAttributeValueDto {
    private Integer id;
    private Integer attributeValueId;
    private String attributeValueName;
    private Integer attributeId;
    private String attributeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
