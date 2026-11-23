package com.backend.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactReplyRequest(
        @NotBlank @Size(max = 200) @Pattern(regexp = "^[^\\r\\n]*$") String subject,
        @NotBlank @Size(max = 5000) String message) {}
