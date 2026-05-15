package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CartDto;
import com.backend.ecommerce.entity.Cart;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.CartRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CartDto> findAll() {
        return cartRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public CartDto create(CartDto dto) {
        Cart entity = new Cart();
        applyDtoToEntity(dto, entity);
        return toDto(cartRepository.save(entity));
    }

    @Override
    public CartDto update(Integer id, CartDto dto) {
        Cart entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(cartRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Cart entity = getEntityById(id);
        cartRepository.delete(entity);
    }

    private Cart getEntityById(Integer id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + id));
    }

    private void applyDtoToEntity(CartDto dto, Cart entity) {
        if (dto.getUserId() != null) {
            entity.setUser(userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId())));
        } else {
            entity.setUser(null);
        }
    }

    private CartDto toDto(Cart entity) {
        CartDto dto = new CartDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
