package com.backend.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminImageUpdateRequest {
    private String alt;
    private Boolean isMain;
    private Integer displayOrder;
    private Integer visualAttributeValueId;
}
