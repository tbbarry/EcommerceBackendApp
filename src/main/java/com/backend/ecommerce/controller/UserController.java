package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ChangePasswordRequest;
import com.backend.ecommerce.dto.UserDto;
import com.backend.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ADMIN uniquement : voir tous les utilisateurs
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // ADMIN ou utilisateur propriétaire
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    // ADMIN uniquement
    // Si tu as déjà /auth/register, alors create ici doit rester admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    // ADMIN ou utilisateur propriétaire
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Integer id, @Valid @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    // ADMIN uniquement
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        userService.changePassword(email, request);

        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }
}
