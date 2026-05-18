package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAddress extends BaseEntity{

    private String address;
    private String zipcode;
    private String city;
    private String state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;
}