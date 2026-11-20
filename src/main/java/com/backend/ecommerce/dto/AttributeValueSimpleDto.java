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
public class AttributeValueSimpleDto {
    private Integer id;
    private String value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
