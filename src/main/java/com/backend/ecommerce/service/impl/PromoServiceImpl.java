package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.PromoDto;
import com.backend.ecommerce.entity.Promo;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.PromoRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import com.backend.ecommerce.service.PromoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoServiceImpl implements PromoService {

    private final PromoRepository promoRepository;
    private final ProductPromoRepository productPromoRepository;
    private final VariantPromoRepository variantPromoRepository;
    private final CatalogProductPricingIndexService catalogProductPricingIndexService;

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
        validatePromo(dto, null);
        Promo entity = new Promo();
        applyDtoToEntity(dto, entity);
        Promo saved = promoRepository.save(entity);
        refreshCatalogPricingForPromo(saved.getId());
        return toDto(saved);
    }

    @Override
    public PromoDto update(Integer id, PromoDto dto) {
        Promo entity = getEntityForUpdate(id);
        validatePromo(dto, id);
        applyDtoToEntity(dto, entity);
        Promo saved = promoRepository.save(entity);
        refreshCatalogPricingForPromo(saved.getId());
        return toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        Promo entity = getEntityForUpdate(id);
        Set<Integer> impactedProductIds = findImpactedProductIds(id);
        // Retirer les associations avant de recalculer les prix sans cette promo.
        productPromoRepository.deleteByPromoId(id);
        variantPromoRepository.deleteByPromoId(id);
        promoRepository.delete(entity);
        promoRepository.flush();
        catalogProductPricingIndexService.refreshProducts(impactedProductIds);
    }

    private void refreshCatalogPricingForPromo(Integer promoId) {
        if (promoId == null) {
            return;
        }

        catalogProductPricingIndexService.refreshProducts(findImpactedProductIds(promoId));
    }

    private Set<Integer> findImpactedProductIds(Integer promoId) {
        Set<Integer> impactedProductIds = new HashSet<>();
        impactedProductIds.addAll(productPromoRepository.findProductIdsByPromoId(promoId));
        impactedProductIds.addAll(variantPromoRepository.findProductIdsByPromoId(promoId));
        return impactedProductIds;
    }

    private Promo getEntityById(Integer id) {
        return promoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo not found with id: " + id));
    }

    private Promo getEntityForUpdate(Integer id) {
        return promoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo not found with id: " + id));
    }

    private void applyDtoToEntity(PromoDto dto, Promo entity) {
        entity.setCode(dto.getCode().trim().toUpperCase(Locale.ROOT));
        entity.setDiscountValue(dto.getDiscountValue());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setIsActive(dto.getIsActive());
    }

    private void validatePromo(PromoDto dto, Integer currentId) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new BusinessException("Le nom de la promotion est obligatoire.", "PROMO_CODE_REQUIRED");
        }
        promoRepository.findByCodeIgnoreCase(dto.getCode().trim())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new BusinessException("Ce nom de promotion existe déjà.", "PROMO_CODE_EXISTS");
                });
        if (dto.getDiscountValue() == null || dto.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0
                || dto.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0
                || dto.getDiscountValue().stripTrailingZeros().scale() > 2) {
            throw new BusinessException("La remise doit être comprise entre 0,01 et 100 %.", "PROMO_DISCOUNT_INVALID");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null || !dto.getEndDate().isAfter(dto.getStartDate())) {
            throw new BusinessException("La date de fin doit être postérieure à la date de début.", "PROMO_DATES_INVALID");
        }
        if (dto.getIsActive() == null) {
            throw new BusinessException("Le statut de la promotion est obligatoire.", "PROMO_STATUS_REQUIRED");
        }
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

    @Transactional(readOnly = true)
    @Override
    public List<PromoDto> findAllActive() {
        LocalDateTime now = LocalDateTime.now();

        return promoRepository.findAllActive(now)
                .stream()
                .map(this::toDto)
                .toList();
    }
}
