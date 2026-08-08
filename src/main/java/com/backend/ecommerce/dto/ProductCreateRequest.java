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

    @Min(0)
    private Integer stock;

    private List<String> colors = new ArrayList<>();

    private List<String> sizes = new ArrayList<>();

    @Valid
    private List<VariantRequest> variants = new ArrayList<>();

    private List<String> imageUrls = new ArrayList<>();
}
