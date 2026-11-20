package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeWithValuesDto {
    private Integer id;
    private String name;
    private List<AttributeValueSimpleDto> values = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
