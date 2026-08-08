package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.DeliveryAddressDto;
import com.backend.ecommerce.dto.MyDeliveryAddressDto;
import com.backend.ecommerce.dto.UserAddressResponse;
import com.backend.ecommerce.entity.DeliveryAddress;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.DeliveryAddressRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.service.DeliveryAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryAddressServiceImpl implements DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryAddressDto> findAll() {
        return deliveryAddressRepository.findByDeletedFalse().stream().map(this::toDto).toList();
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
        softDelete(entity);
        ensureDefaultAddressAfterDeletion(entity);
    }

    private DeliveryAddress getEntityById(Integer id) {
        return deliveryAddressRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryAddress not found with id: " + id));
    }

    private void softDelete(DeliveryAddress entity) {
        if (entity.isDeleted()) {
            throw new BusinessException("Cette adresse est deja supprimee");
        }
        entity.setDeleted(true);
        entity.setDefaultAddress(false);
        deliveryAddressRepository.save(entity);
    }

    private void applyDtoToEntity(DeliveryAddressDto dto, DeliveryAddress entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setAddress(dto.getAddress());
        entity.setZipcode(dto.getZipcode());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPhone(dto.getPhone());
        entity.setLabel(dto.getLabel());
        entity.setCountry(dto.getCountry().toUpperCase(Locale.ROOT));

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));
            entity.setUser(user);
            applyDefaultAddressRule(user, entity, dto.isDefaultAddress());
        } else {
            entity.setUser(null);
            entity.setDefaultAddress(false);
        }
    }

    private DeliveryAddressDto toDto(DeliveryAddress entity) {
        DeliveryAddressDto dto = new DeliveryAddressDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setAddress(entity.getAddress());
        dto.setZipcode(entity.getZipcode());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPhone(entity.getPhone());
        dto.setLabel(entity.getLabel());
        dto.setCountry(entity.getCountry());
        dto.setDefaultAddress(entity.isDefaultAddress());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyDeliveryAddressDto> findMyAddresses(String email) {
        return deliveryAddressRepository.findByUserEmailAndDeletedFalse(email).stream()
                .map(this::toMyDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MyDeliveryAddressDto findMyAddressById(String email, Integer id) {
        return toMyDto(getOwnedAddress(id, email));
    }

    @Override
    public MyDeliveryAddressDto createMyAddress(String email, MyDeliveryAddressDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        DeliveryAddress entity = new DeliveryAddress();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setAddress(dto.getAddress());
        entity.setZipcode(dto.getZipcode());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPhone(dto.getPhone());
        entity.setLabel(dto.getLabel());
        entity.setCountry(dto.getCountry().toUpperCase(Locale.ROOT));
        entity.setUser(user);
        applyDefaultAddressRule(user, entity, dto.isDefaultAddress());

        return toMyDto(deliveryAddressRepository.save(entity));
    }

    @Override
    public MyDeliveryAddressDto updateMyAddress(String email, Integer id, MyDeliveryAddressDto dto) {
        DeliveryAddress entity = getOwnedAddress(id, email);
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setAddress(dto.getAddress());
        entity.setZipcode(dto.getZipcode());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPhone(dto.getPhone());
        entity.setLabel(dto.getLabel());
        entity.setCountry(dto.getCountry().toUpperCase(Locale.ROOT));
        applyDefaultAddressRule(entity.getUser(), entity, dto.isDefaultAddress());
        return toMyDto(deliveryAddressRepository.save(entity));
    }

    @Override
    public void deleteMyAddress(String email, Integer id) {
        DeliveryAddress entity = getOwnedAddress(id, email);
        softDelete(entity);
        ensureDefaultAddressAfterDeletion(entity);
    }

    private void ensureDefaultAddressAfterDeletion(DeliveryAddress deletedAddress) {
        if (deletedAddress.getUser() == null) {
            return;
        }

        List<DeliveryAddress> remaining = deliveryAddressRepository.findByUserIdAndDeletedFalse(deletedAddress.getUser().getId());
        if (remaining.isEmpty()) {
            return;
        }

        boolean hasDefault = remaining.stream().anyMatch(DeliveryAddress::isDefaultAddress);
        if (!hasDefault) {
            DeliveryAddress nextDefault = remaining.stream().findFirst().orElse(null);
            if (nextDefault != null) {
                nextDefault.setDefaultAddress(true);
                deliveryAddressRepository.save(nextDefault);
            }
        }
    }

    private DeliveryAddress getOwnedAddress(Integer id, String email) {
        return deliveryAddressRepository.findByIdAndUserEmailAndDeletedFalse(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryAddress not found with id: " + id));
    }

    private MyDeliveryAddressDto toMyDto(DeliveryAddress entity) {
        MyDeliveryAddressDto dto = new MyDeliveryAddressDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setAddress(entity.getAddress());
        dto.setZipcode(entity.getZipcode());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPhone(entity.getPhone());
        dto.setLabel(entity.getLabel());
        dto.setCountry(entity.getCountry());
        dto.setDefaultAddress(entity.isDefaultAddress());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> findMyUserAddresses(String email) {
        return deliveryAddressRepository.findByUserEmailAndDeletedFalse(email).stream()
                .map(this::toUserAddressResponse)
                .toList();
    }

    private UserAddressResponse toUserAddressResponse(DeliveryAddress entity) {
        return UserAddressResponse.builder()
                .id(entity.getId())
                .label(entity.getLabel())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phone(entity.getPhone())
                .street(entity.getAddress())
                .city(entity.getCity())
                .state(entity.getState())
                .zipCode(entity.getZipcode())
                .country(entity.getCountry())
                .defaultAddress(entity.isDefaultAddress())
                .build();
    }

    private void applyDefaultAddressRule(User user, DeliveryAddress entity, boolean requestedDefault) {
        if (user == null) {
            entity.setDefaultAddress(false);
            return;
        }

        List<DeliveryAddress> userAddresses = deliveryAddressRepository.findByUserIdAndDeletedFalse(user.getId());
        List<DeliveryAddress> otherAddresses = userAddresses.stream()
                .filter(address -> !address.getId().equals(entity.getId()))
                .toList();

        if (requestedDefault) {
            otherAddresses.forEach(address -> address.setDefaultAddress(false));
            entity.setDefaultAddress(true);
            if (!otherAddresses.isEmpty()) {
                deliveryAddressRepository.saveAll(otherAddresses);
            }
            return;
        }

        if (entity.getId() == null && userAddresses.isEmpty()) {
            entity.setDefaultAddress(true);
            return;
        }

        if (entity.isDefaultAddress() && !otherAddresses.isEmpty()) {
            DeliveryAddress fallback = otherAddresses.stream().findFirst().orElse(null);
            if (fallback != null) {
                fallback.setDefaultAddress(true);
                deliveryAddressRepository.save(fallback);
            }
            entity.setDefaultAddress(false);
            return;
        }

        entity.setDefaultAddress(false);
    }
}
