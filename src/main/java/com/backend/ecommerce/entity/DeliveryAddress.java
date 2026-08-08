package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "delivery_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAddress extends BaseEntity{

    @NotBlank
    @Column(nullable = false, length = 80)
    private String firstName;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String lastName;

    @NotBlank
    @Column(nullable = false)
    private String address;

    @NotBlank
    @Column(nullable = false)
    private String zipcode;

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String state;

    @NotBlank
    @Column(nullable = false)
    private String phone;

    @NotBlank
    @Column(nullable = false, length = 2)
    private String country;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String label;

    @Column(name = "is_default", nullable = false)
    private boolean defaultAddress;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}