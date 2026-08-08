package com.backend.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyDeliveryAddressDto {
    private Integer id;

    @NotBlank
    @Size(max = 80)
    private String firstName;

    @NotBlank
    @Size(max = 80)
    private String lastName;

    @NotBlank
    @Size(max = 120)
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\d{5}(?:-\\d{4})?$", message = "Zipcode US invalide (12345 ou 12345-6789)")
    private String zipcode;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z .'-]{2,100}$", message = "Ville invalide")
    private String city;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$", message = "State doit etre un code US a 2 lettres (ex: CA)")
    private String state;

    @NotBlank
    @Pattern(regexp = "^(\\+1[-. ]?)?\\(?\\d{3}\\)?[-. ]?\\d{3}[-. ]?\\d{4}$", message = "Telephone US invalide")
    private String phone;

    @NotBlank
    @Size(max = 30)
    private String label;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country doit etre un code ISO-2 (ex: US)")
    private String country;

    private boolean defaultAddress;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}