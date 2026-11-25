package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.PromoDto;
import com.backend.ecommerce.service.impl.PromoServiceExtendedImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/promos")
@RequiredArgsConstructor
public class AdminPromoManagementController {

    private final PromoServiceExtendedImpl promoService;

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivatePromo(@PathVariable Integer id) {
        PromoDto promo = promoService.deactivatePromo(id);
        return ResponseEntity.ok(new ApiResponse("Promo deactivated", promo));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activatePromo(@PathVariable Integer id) {
        PromoDto promo = promoService.activatePromo(id);
        return ResponseEntity.ok(new ApiResponse("Promo activated", promo));
    }

    static class ApiResponse {
        public String message;
        public Object data;

        public ApiResponse(String message, Object data) {
            this.message = message;
            this.data = data;
        }
    }
}
