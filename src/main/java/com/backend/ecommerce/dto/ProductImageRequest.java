package com.backend.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {

    @NotBlank
    private String url;

    private String alt;

    @JsonProperty("isMain")
    private boolean isMain = false;

    private Integer displayOrder = 0;

    // Optionnel : ID de la valeur d'attribut visuelle (ex: couleur rouge)
    private Integer visualAttributeValueId;
}
