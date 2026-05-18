package com.backend.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxSettingDto {

    private Integer id;

    @NotNull(message = "Le taux de taxe est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le taux de taxe ne peut pas être négatif")
    private BigDecimal rate;

    private Boolean active;
}