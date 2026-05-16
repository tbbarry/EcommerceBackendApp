package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CartItemDto;
import com.backend.ecommerce.entity.CartItem;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.CartItemRepository;
import com.backend.ecommerce.repository.CartRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final VariantRepository variantRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> findAll() {
        return cartItemRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CartItemDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public CartItemDto create(CartItemDto dto) {
        CartItem entity = new CartItem();
        applyDtoToEntity(dto, entity);
        return toDto(cartItemRepository.save(entity));
    }

    @Override
    public CartItemDto update(Integer id, CartItemDto dto) {
        CartItem entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(cartItemRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        CartItem entity = getEntityById(id);
        cartItemRepository.delete(entity);
    }

    private CartItem getEntityById(Integer id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: " + id));
    }

    private void applyDtoToEntity(CartItemDto dto, CartItem entity) {
        entity.setQuantity(dto.getQuantity());
        if (dto.getCartId() != null) {
            entity.setCart(cartRepository.findById(dto.getCartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + dto.getCartId())));
        } else {
            entity.setCart(null);
        }
        if (dto.getVariantId() != null) {
            entity.setVariant(variantRepository.findById(dto.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + dto.getVariantId())));
        } else {
            entity.setVariant(null);
        }
    }

    private CartItemDto toDto(CartItem entity) {
        CartItemDto dto = new CartItemDto();
        dto.setId(entity.getId());
        dto.setQuantity(entity.getQuantity());
        dto.setCartId(entity.getCart() != null ? entity.getCart().getId() : null);
        dto.setVariantId(entity.getVariant() != null ? entity.getVariant().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }


}
