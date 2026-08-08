package com.backend.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotNull
    private Integer addressId;

    @NotNull
    private Integer shippingMethodId;

    private boolean leaveAtDoor;

    private boolean requireSignature;

    @Size(max = 300)
    private String deliveryNote;

    @Size(max = 60)
    private String couponCode;
}
