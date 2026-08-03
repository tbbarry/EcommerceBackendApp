package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ChangePasswordRequest;
import com.backend.ecommerce.dto.UserDto;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public UserDto create(UserDto dto) {
        User entity = new User();
        applyDtoToEntity(dto, entity);
        return toDto(userRepository.save(entity));
    }

    @Override
    public UserDto update(Integer id, UserDto dto) {
        User entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(userRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        User entity = getEntityById(id);
        userRepository.delete(entity);
    }

    private User getEntityById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private void applyDtoToEntity(UserDto dto, User entity) {
        entity.setFirstname(dto.getFirstname());
        entity.setLastname(dto.getLastname());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }

    private UserDto toDto(User entity) {
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setFirstname(entity.getFirstname());
        dto.setLastname(entity.getLastname());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setPassword(entity.getPassword());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    public void changePassword(String email, @Valid ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // vérifier ancien mot de passe
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        // éviter même mot de passe
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit être différent");
        }

        //  encoder nouveau mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
}
