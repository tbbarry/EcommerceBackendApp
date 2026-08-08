package com.backend.ecommerce.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartSyncRequest {
    @Valid
    private List<CartItemUpsertRequest> items = new ArrayList<>();

    private boolean replaceExisting;
}
