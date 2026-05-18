package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.TaxSettingDto;
import com.backend.ecommerce.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/tax-settings")
@RequiredArgsConstructor
public class TaxSettingController {

    private final TaxService taxSettingService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/current")
    public ResponseEntity<TaxSettingDto> updateCurrentTaxRate(@RequestBody TaxSettingDto dto) {
        return ResponseEntity.ok(taxSettingService.updateCurrentTaxRate(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/current")
    public ResponseEntity<BigDecimal> getCurrentTaxRate() {
        return ResponseEntity.ok(taxSettingService.getCurrentTaxRate());
    }
}
