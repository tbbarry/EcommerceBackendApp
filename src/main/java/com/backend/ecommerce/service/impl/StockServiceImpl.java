package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.StockDto;
import com.backend.ecommerce.entity.Stock;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final VariantRepository variantRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockDto> findAll() {
        return stockRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public StockDto create(StockDto dto) {
        Stock entity = new Stock();
        applyDtoToEntity(dto, entity);
        return toDto(stockRepository.save(entity));
    }

    @Override
    public StockDto update(Integer id, StockDto dto) {
        Stock entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(stockRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Stock entity = getEntityById(id);
        stockRepository.delete(entity);
    }

    private Stock getEntityById(Integer id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + id));
    }

    private void applyDtoToEntity(StockDto dto, Stock entity) {
        entity.setQuantity(dto.getQuantity());
        if (dto.getVariantId() != null) {
            Variant variant = variantRepository.findById(dto.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + dto.getVariantId()));
            variant.setStock(dto.getQuantity() == null ? 0 : Math.max(0, dto.getQuantity()));
            variantRepository.save(variant);
            entity.setVariant(variant);
        } else {
            entity.setVariant(null);
        }
    }

    private StockDto toDto(Stock entity) {
        StockDto dto = new StockDto();
        dto.setId(entity.getId());
        dto.setQuantity(entity.getVariant() != null ? entity.getVariant().getStock() : entity.getQuantity());
        dto.setVariantId(entity.getVariant() != null ? entity.getVariant().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
