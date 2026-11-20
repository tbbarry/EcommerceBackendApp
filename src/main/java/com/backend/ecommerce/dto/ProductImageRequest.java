package com.backend.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String alt;

    private boolean isMain = false;

    private Integer displayOrder = 0;

    // Optionnel : ID de la valeur d'attribut visuelle (ex: couleur rouge)
    private Integer visualAttributeValueId;
}
