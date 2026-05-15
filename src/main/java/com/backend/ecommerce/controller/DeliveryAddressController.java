package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.DeliveryAddressDto;
import com.backend.ecommerce.service.DeliveryAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/delivery-addresses")
@RequiredArgsConstructor
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;

    @GetMapping
    public ResponseEntity<List<DeliveryAddressDto>> findAll() {
        return ResponseEntity.ok(deliveryAddressService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryAddressDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(deliveryAddressService.findById(id));
    }

    @PostMapping
    public ResponseEntity<DeliveryAddressDto> create(@Valid @RequestBody DeliveryAddressDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryAddressService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryAddressDto> update(@PathVariable Integer id, @Valid @RequestBody DeliveryAddressDto dto) {
        return ResponseEntity.ok(deliveryAddressService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        deliveryAddressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

