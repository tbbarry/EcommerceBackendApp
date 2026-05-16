package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.PromoDto;
import com.backend.ecommerce.entity.Promo;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.PromoRepository;
import com.backend.ecommerce.service.PromoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoServiceImpl implements PromoService {

    private final PromoRepository promoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PromoDto> findAll() {
        return promoRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PromoDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public PromoDto create(PromoDto dto) {
        Promo entity = new Promo();
        applyDtoToEntity(dto, entity);
        return toDto(promoRepository.save(entity));
    }

    @Override
    public PromoDto update(Integer id, PromoDto dto) {
        Promo entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(promoRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Promo entity = getEntityById(id);
        promoRepository.delete(entity);
    }

    private Promo getEntityById(Integer id) {
        return promoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo not found with id: " + id));
    }

    private void applyDtoToEntity(PromoDto dto, Promo entity) {
        entity.setCode(dto.getCode());
        entity.setDiscountValue(dto.getDiscountValue());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setIsActive(dto.getIsActive());
    }

    private PromoDto toDto(Promo entity) {
        PromoDto dto = new PromoDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setDiscountValue(entity.getDiscountValue());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
