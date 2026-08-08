package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CouponDto;
import com.backend.ecommerce.dto.CouponUsageDto;
import com.backend.ecommerce.entity.Coupon;
import com.backend.ecommerce.entity.CouponUsage;
import com.backend.ecommerce.entity.DiscountType;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.CouponRepository;
import com.backend.ecommerce.repository.CouponUsageRepository;
import com.backend.ecommerce.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CouponDto> findAll() {
        return couponRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public CouponDto create(CouponDto dto) {
        validateCouponDefinition(dto);

        if (couponRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new BusinessException("Un coupon avec ce code existe deja");
        }

        Coupon coupon = new Coupon();
        applyDtoToEntity(dto, coupon);
        coupon.setUsedCount(dto.getUsedCount() != null ? dto.getUsedCount() : 0);

        return toDto(couponRepository.save(coupon));
    }

    @Override
    public CouponDto update(Integer id, CouponDto dto) {
        validateCouponDefinition(dto);

        Coupon existing = getEntityById(id);
        couponRepository.findByCodeIgnoreCase(dto.getCode())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new BusinessException("Un coupon avec ce code existe deja");
                });

        Integer preservedUsedCount = existing.getUsedCount() != null ? existing.getUsedCount() : 0;
        applyDtoToEntity(dto, existing);
        existing.setUsedCount(dto.getUsedCount() != null ? dto.getUsedCount() : preservedUsedCount);

        return toDto(couponRepository.save(existing));
    }

    @Override
    public void delete(Integer id) {
        Coupon coupon = getEntityById(id);
        couponRepository.delete(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon validateCoupon(String code, User user, BigDecimal orderAmount) {
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException("Le code coupon est obligatoire");
        }

        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));

        if (!Boolean.TRUE.equals(coupon.getActive())) {
            throw new BusinessException("Ce coupon est inactif");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            throw new BusinessException("Ce coupon n'est pas valide pour la date actuelle");
        }

        BigDecimal safeOrderAmount = orderAmount != null ? orderAmount : BigDecimal.ZERO;
        if (coupon.getMinimumOrderAmount() != null && safeOrderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new BusinessException("Montant minimum non atteint pour ce coupon");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new BusinessException("La limite d'utilisation de ce coupon est atteinte");
        }

        if (Boolean.TRUE.equals(coupon.getOneTimePerUser()) && user != null
                && couponUsageRepository.existsByCouponIdAndUserId(coupon.getId(), user.getId())) {
            throw new BusinessException("Ce coupon est deja utilise par cet utilisateur");
        }

        return coupon;
    }

    @Override
    public void registerUsage(Coupon coupon, User user, Order order) {
        if (coupon == null || user == null || order == null) {
            return;
        }

        CouponUsage usage = CouponUsage.builder()
                .coupon(coupon)
                .user(user)
                .order(order)
                .usedAt(LocalDateTime.now())
                .build();

        couponUsageRepository.save(usage);

        int current = coupon.getUsedCount() != null ? coupon.getUsedCount() : 0;
        coupon.setUsedCount(current + 1);
        couponRepository.save(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponUsageDto> getCouponUsages(Integer couponId) {
        return couponUsageRepository.findByCouponId(couponId).stream()
                .map(this::toUsageDto)
                .toList();
    }

    @Override
    public CouponDto setCouponActive(Integer couponId, boolean active) {
        Coupon coupon = getEntityById(couponId);
        coupon.setActive(active);
        return toDto(couponRepository.save(coupon));
    }

    private Coupon getEntityById(Integer id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
    }

    private void applyDtoToEntity(CouponDto dto, Coupon entity) {
        entity.setCode(dto.getCode().trim().toUpperCase(Locale.ROOT));
        entity.setDescription(dto.getDescription());
        entity.setDiscountType(dto.getDiscountType());
        entity.setDiscountValue(dto.getDiscountValue());
        entity.setMinimumOrderAmount(dto.getMinimumOrderAmount());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setActive(dto.getActive());
        entity.setUsageLimit(dto.getUsageLimit());
        entity.setOneTimePerUser(dto.getOneTimePerUser());
        entity.setUsedCount(dto.getUsedCount() != null ? dto.getUsedCount() : 0);
    }

    private CouponDto toDto(Coupon entity) {
        return CouponDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .minimumOrderAmount(entity.getMinimumOrderAmount())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .active(entity.getActive())
                .usageLimit(entity.getUsageLimit())
                .usedCount(entity.getUsedCount())
                .oneTimePerUser(entity.getOneTimePerUser())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private CouponUsageDto toUsageDto(CouponUsage usage) {
        return CouponUsageDto.builder()
                .id(usage.getId())
                .couponId(usage.getCoupon().getId())
                .couponCode(usage.getCoupon().getCode())
                .userId(usage.getUser().getId())
                .orderId(usage.getOrder().getId())
                .usedAt(usage.getUsedAt())
                .createdAt(usage.getCreatedAt())
                .build();
    }

    private void validateCouponDefinition(CouponDto dto) {
        if (dto.getStartDate() != null && dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("La date de fin doit etre superieure a la date de debut");
        }

        if (dto.getDiscountType() == DiscountType.PERCENTAGE
                && dto.getDiscountValue() != null
                && dto.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("Un coupon percentage ne peut pas depasser 100%");
        }

        if (dto.getDiscountType() == DiscountType.FREE_SHIPPING
                && dto.getDiscountValue() != null
                && dto.getDiscountValue().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("FREE_SHIPPING doit avoir discountValue = 0");
        }
    }
}
