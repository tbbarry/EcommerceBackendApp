package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddressResponse {

    private Integer id;
    private String label;
    private String firstName;
    private String lastName;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private boolean defaultAddress;
}
