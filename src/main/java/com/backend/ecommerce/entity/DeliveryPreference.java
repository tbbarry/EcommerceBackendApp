package com.backend.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "delivery_preferences")
@Getter 
@Setter     
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPreference extends BaseEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orderId", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private boolean leaveAtDoor;

    @Column(nullable = false)
    private boolean requireSignature;

    @Column(columnDefinition = "TEXT")
    private String deliveryNote;
}
