package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressDto {
    private Integer id;
    private String address;
    private String zipcode;
    private String city;
    private String state;
    private Integer userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
