package com.backend.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank
    private String oldPassword;

    @NotBlank
      @NotBlank
    @Size(min = 8)
    @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
      message = "Weak password"
    )
    private String newPassword;

}
