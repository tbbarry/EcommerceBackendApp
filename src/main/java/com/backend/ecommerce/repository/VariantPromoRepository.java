package com.backend.ecommerce.repository;

import com.backend.ecommerce.dto.VariantDiscountRow;
import com.backend.ecommerce.entity.VariantPromo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VariantPromoRepository extends JpaRepository<VariantPromo, Integer> {

    @Query("""
        SELECT vp
        FROM VariantPromo vp
        JOIN FETCH vp.promo p
        JOIN FETCH vp.variant v
        WHERE v.product.id = :productId
          AND p.isActive = true
          AND p.startDate <= :now
          AND p.endDate >= :now
    """)
    List<VariantPromo> findActiveByProductId(
            @Param("productId") Integer productId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT new com.backend.ecommerce.dto.VariantDiscountRow(vp.variant.id, MAX(p.discountValue))
        FROM VariantPromo vp
        JOIN vp.promo p
        WHERE vp.variant.product.id = :productId
          AND p.isActive = true
          AND p.startDate <= :now
          AND p.endDate >= :now
        GROUP BY vp.variant.id
    """)
    List<VariantDiscountRow> findActiveMaxDiscountPercentByProductId(
            @Param("productId") Integer productId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT DISTINCT vp.variant.product.id
        FROM VariantPromo vp
        WHERE vp.promo.id = :promoId
    """)
    List<Integer> findProductIdsByPromoId(@Param("promoId") Integer promoId);
}
