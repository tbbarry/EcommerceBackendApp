package com.backend.ecommerce.dto;

import com.backend.ecommerce.entity.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto {

    private Integer id;

    @NotBlank
    private String code;

    private String description;

    @NotNull
    private DiscountType discountType;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal discountValue;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal minimumOrderAmount;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    private Boolean active;

    @Min(0)
    private Integer usageLimit;

    @Min(0)
    private Integer usedCount;

    @NotNull
    private Boolean oneTimePerUser;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
