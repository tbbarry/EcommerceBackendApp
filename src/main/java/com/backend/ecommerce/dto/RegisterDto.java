package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    private Integer id;
    private String firstname;
    private String lastname;
    @NotBlank
    @Email(message = "Email invalide")
    private String email;
    @NotBlank
    @Size(min = 8)
    @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
      message = "Weak password"
    )
    private String password;
}
