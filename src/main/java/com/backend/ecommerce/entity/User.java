package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String password;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<DeliveryAddress> deliveryAddresses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;
}