package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ChangePasswordRequest;
import com.backend.ecommerce.dto.RegisterDto;
import com.backend.ecommerce.dto.UpdateUserProfileRequest;
import com.backend.ecommerce.dto.UserProfileDto;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.exception.BusinessException;
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
    public List<RegisterDto> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RegisterDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public RegisterDto create(RegisterDto dto) {
        User entity = new User();
        applyDtoToEntity(dto, entity);
        return toDto(userRepository.save(entity));
    }

    @Override
    public RegisterDto update(Integer id, RegisterDto dto) {
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

    private void applyDtoToEntity(RegisterDto dto, User entity) {
        entity.setFirstname(dto.getFirstname());
        entity.setLastname(dto.getLastname());
        entity.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }

    private RegisterDto toDto(User entity) {
        RegisterDto dto = new RegisterDto();
        dto.setId(entity.getId());
        dto.setFirstname(entity.getFirstname());
        dto.setLastname(entity.getLastname());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile(String email) {
        User user = getEntityByEmail(email);
        return toProfileDto(user);
    }

    @Override
    public UserProfileDto updateCurrentUserProfile(String email, UpdateUserProfileRequest request) {
        User user = getEntityByEmail(email);
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPhone(request.getPhone());


        return toProfileDto(userRepository.save(user));
    }

    @Override
    public void deleteCurrentUser(String email) {
        User user = getEntityByEmail(email);
        userRepository.delete(user);
    }

    private User getEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private UserProfileDto toProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
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
