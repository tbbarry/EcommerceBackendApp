package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.config.TaxProperties;
import com.backend.ecommerce.dto.TaxSettingDto;
import com.backend.ecommerce.entity.TaxSetting;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.TaxSettingRepository;
import com.backend.ecommerce.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxServiceImpl implements TaxService {

    private final TaxSettingRepository taxSettingRepository;
    private final TaxProperties taxProperties;

    public BigDecimal getCurrentTaxRate() {
        return taxSettingRepository.findByActiveTrue()
                .map(TaxSetting::getRate)
                .orElse(taxProperties.getRate());
    }

    @Override
    public List<TaxSettingDto> findAll() {
        return taxSettingRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public TaxSettingDto findById(Integer id) {
        TaxSetting entity = taxSettingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxSetting not found with id: " + id));

        return toDto(entity);
    }

    @Override
    public TaxSettingDto create(TaxSettingDto dto) {
        TaxSetting entity = new TaxSetting();

        entity.setRate(dto.getRate());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);

        TaxSetting saved = taxSettingRepository.save(entity);

        return toDto(saved);
    }

    @Override
    public TaxSettingDto update(Integer id, TaxSettingDto dto) {
        TaxSetting entity = taxSettingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxSetting not found with id: " + id));

        entity.setRate(dto.getRate());

        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }

        TaxSetting saved = taxSettingRepository.save(entity);

        return toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        TaxSetting entity = taxSettingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxSetting not found with id: " + id));

        taxSettingRepository.delete(entity);
    }

    @Override
    public TaxSettingDto getCurrentTaxSetting() {
        return taxSettingRepository.findByActiveTrue()
                .map(this::toDto)
                .orElse(
                        TaxSettingDto.builder()
                                .rate(taxProperties.getRate())
                                .active(true)
                                .build()
                );
    }

    @Override
    public TaxSettingDto updateCurrentTaxRate(TaxSettingDto dto) {
        TaxSetting current = taxSettingRepository.findByActiveTrue()
                .orElseGet(() -> {
                    TaxSetting taxSetting = new TaxSetting();
                    taxSetting.setActive(true);
                    return taxSetting;
                });

        current.setRate(dto.getRate());
        current.setActive(true);

        TaxSetting saved = taxSettingRepository.save(current);

        return toDto(saved);
    }

    private TaxSettingDto toDto(TaxSetting entity) {
        return TaxSettingDto.builder()
                .id(entity.getId())
                .rate(entity.getRate())
                .active(entity.getActive())
                .build();
    }
}
