package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.ProductPromo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductPromoRepository extends JpaRepository<ProductPromo, Integer> {

	boolean existsByPromoIdAndProductId(Integer promoId, Integer productId);

	@Query("SELECT pp FROM ProductPromo pp JOIN FETCH pp.product WHERE pp.promo.id = :promoId")
	List<ProductPromo> findAssignmentsByPromoId(@Param("promoId") Integer promoId);

	@Query("""
		SELECT DISTINCT pp.product.id FROM ProductPromo pp
		JOIN pp.promo p
		LEFT JOIN CatalogProduct cp ON cp.productId = pp.product.id
		WHERE p.isActive = true AND (cp.id IS NULL
			OR (p.startDate > cp.updatedAt AND p.startDate <= :now)
			OR (p.endDate >= cp.updatedAt AND p.endDate < :now))
	""")
	List<Integer> findProductsWithDuePromotionChanges(@Param("now") LocalDateTime now);

	@Modifying
	@Query("DELETE FROM ProductPromo pp WHERE pp.promo.id = :promoId")
	void deleteByPromoId(@Param("promoId") Integer promoId);

	@Query("""
		SELECT pp
		FROM ProductPromo pp
		JOIN FETCH pp.promo p
		WHERE pp.product.id = :productId
		  AND p.isActive = true
		  AND p.startDate <= :now
		  AND p.endDate >= :now
	""")
	List<ProductPromo> findActiveByProductId(
			@Param("productId") Integer productId,
			@Param("now") LocalDateTime now
	);

	@Query("""
		SELECT MAX(p.discountValue)
		FROM ProductPromo pp
		JOIN pp.promo p
		WHERE pp.product.id = :productId
		  AND p.isActive = true
		  AND p.startDate <= :now
		  AND p.endDate >= :now
	""")
	BigDecimal findActiveGlobalMaxDiscountPercentByProductId(
			@Param("productId") Integer productId,
			@Param("now") LocalDateTime now
	);

	@Query("""
		SELECT DISTINCT pp.product.id
		FROM ProductPromo pp
		WHERE pp.promo.id = :promoId
	""")
	List<Integer> findProductIdsByPromoId(@Param("promoId") Integer promoId);
}
