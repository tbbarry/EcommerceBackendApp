package com.backend.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
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
public class AttributeDto {
    private Integer id;
    @NotBlank
    private String name;
    private List<AttributeValueDto> values = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
