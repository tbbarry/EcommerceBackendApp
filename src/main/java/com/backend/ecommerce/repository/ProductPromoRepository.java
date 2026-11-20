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
