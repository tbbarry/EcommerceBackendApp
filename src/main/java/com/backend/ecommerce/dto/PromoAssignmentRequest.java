package com.backend.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PromoAssignmentRequest(@NotBlank String type, @NotNull @Positive Integer targetId) {}
