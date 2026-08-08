package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.UserAddressResponse;
import com.backend.ecommerce.service.DeliveryAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/addresses")
@RequiredArgsConstructor
public class UserShippingAddressController {

    private final DeliveryAddressService deliveryAddressService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> findMyUserAddresses() {
        return ResponseEntity.ok(deliveryAddressService.findMyUserAddresses(currentUserEmail()));
    }

    private String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
