package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.DeliveryAddressDto;
import com.backend.ecommerce.entity.DeliveryAddress;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.DeliveryAddressRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.service.DeliveryAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryAddressServiceImpl implements DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryAddressDto> findAll() {
        return deliveryAddressRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryAddressDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public DeliveryAddressDto create(DeliveryAddressDto dto) {
        DeliveryAddress entity = new DeliveryAddress();
        applyDtoToEntity(dto, entity);
        return toDto(deliveryAddressRepository.save(entity));
    }

    @Override
    public DeliveryAddressDto update(Integer id, DeliveryAddressDto dto) {
        DeliveryAddress entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(deliveryAddressRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        DeliveryAddress entity = getEntityById(id);
        deliveryAddressRepository.delete(entity);
    }

    private DeliveryAddress getEntityById(Integer id) {
        return deliveryAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryAddress not found with id: " + id));
    }

    private void applyDtoToEntity(DeliveryAddressDto dto, DeliveryAddress entity) {
        entity.setAddress(dto.getAddress());
        entity.setZipcode(dto.getZipcode());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        if (dto.getUserId() != null) {
            entity.setUser(userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId())));
        } else {
            entity.setUser(null);
        }
    }

    private DeliveryAddressDto toDto(DeliveryAddress entity) {
        DeliveryAddressDto dto = new DeliveryAddressDto();
        dto.setId(entity.getId());
        dto.setAddress(entity.getAddress());
        dto.setZipcode(entity.getZipcode());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
