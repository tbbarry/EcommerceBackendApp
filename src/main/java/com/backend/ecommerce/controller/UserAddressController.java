package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.MyDeliveryAddressDto;
import com.backend.ecommerce.service.DeliveryAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/user/address/me"})
@RequiredArgsConstructor
public class UserAddressController {

    private final DeliveryAddressService deliveryAddressService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<MyDeliveryAddressDto>> findMyAddresses() {
        return ResponseEntity.ok(deliveryAddressService.findMyAddresses(currentUserEmail()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<MyDeliveryAddressDto> findMyAddressById(@PathVariable Integer id) {
        return ResponseEntity.ok(deliveryAddressService.findMyAddressById(currentUserEmail(), id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<MyDeliveryAddressDto> createMyAddress(@Valid @RequestBody MyDeliveryAddressDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryAddressService.createMyAddress(currentUserEmail(), dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<MyDeliveryAddressDto> updateMyAddress(
            @PathVariable Integer id,
            @Valid @RequestBody MyDeliveryAddressDto dto
    ) {
        return ResponseEntity.ok(deliveryAddressService.updateMyAddress(currentUserEmail(), id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyAddress(@PathVariable Integer id) {
        deliveryAddressService.deleteMyAddress(currentUserEmail(), id);
        return ResponseEntity.noContent().build();
    }

    private String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
