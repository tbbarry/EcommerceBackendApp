package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String firstname;

    @NotBlank
    @Column(nullable = false)
    private String lastname;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String phone;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean enabled;

    @Valid
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<DeliveryAddress> deliveryAddresses = new ArrayList<>();

    @Valid
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @Valid
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CouponUsage> couponUsages = new ArrayList<>();

    @Valid
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;
}