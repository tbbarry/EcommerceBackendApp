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
public class AttributeValueDto {
    private Integer id;
    private String value;
    private Integer attributeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
