package com.backend.ecommerce.dto;

import com.backend.ecommerce.entity.DeliveryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShippingMethodRequest {
    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 60)
    @Pattern(regexp = "[A-Za-z0-9_-]+")
    private String code;

    @NotNull
    private DeliveryType deliveryType;

    private String description;

    @NotNull
    @Min(0)
    private Integer minDeliveryDays;

    @NotNull
    @Min(0)
    private Integer maxDeliveryDays;

    @NotNull
    @DecimalMin("0.0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    @DecimalMin("0.0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal freeShippingThreshold;

    @NotNull
    private Boolean active;
}
