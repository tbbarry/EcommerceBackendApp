package com.backend.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String value;
    private Integer attributeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
