package com.backend.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    @NotBlank
    private String brand;

    @NotNull
    private Integer categoryId;

    private String slug;

    // Variantes du produit (au moins une requise)
    @Valid
    private List<VariantFullRequest> variants = new ArrayList<>();

    // Images du produit
    @Valid
    private List<ProductImageRequest> images = new ArrayList<>();
}
