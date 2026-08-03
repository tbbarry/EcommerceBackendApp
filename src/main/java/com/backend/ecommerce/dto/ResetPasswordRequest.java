package com.backend.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
    private String token;
    @NotBlank
    @Size(min = 8)
    @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
      message = "Weak password"
    )
    private String newPassword;
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
}