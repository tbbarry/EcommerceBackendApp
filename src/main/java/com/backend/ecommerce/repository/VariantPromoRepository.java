package com.backend.ecommerce.repository;

import com.backend.ecommerce.dto.VariantDiscountRow;
import com.backend.ecommerce.entity.VariantPromo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VariantPromoRepository extends JpaRepository<VariantPromo, Integer> {

    boolean existsByPromoIdAndVariantId(Integer promoId, Integer variantId);

    @Modifying
    @Query("DELETE FROM VariantPromo vp WHERE vp.variant.id IN :variantIds")
    void deleteByVariantIds(@Param("variantIds") List<Integer> variantIds);

    @Query("SELECT vp FROM VariantPromo vp JOIN FETCH vp.variant v JOIN FETCH v.product WHERE vp.promo.id = :promoId")
    List<VariantPromo> findAssignmentsByPromoId(@Param("promoId") Integer promoId);

    @Query("""
        SELECT DISTINCT vp.variant.product.id FROM VariantPromo vp
        JOIN vp.promo p
        LEFT JOIN CatalogProduct cp ON cp.productId = vp.variant.product.id
        WHERE p.isActive = true AND (cp.id IS NULL
            OR (p.startDate > cp.updatedAt AND p.startDate <= :now)
            OR (p.endDate >= cp.updatedAt AND p.endDate < :now))
    """)
    List<Integer> findProductsWithDuePromotionChanges(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM VariantPromo vp WHERE vp.promo.id = :promoId")
    void deleteByPromoId(@Param("promoId") Integer promoId);

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
