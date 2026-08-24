package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant, Integer> {

    Optional<Variant> findBySku(String sku);

    boolean existsBySku(String sku);

  /* 
    boolean existsByProductIdAndProductColorNameIgnoreCaseAndSizeIgnoreCase(
        Integer productId,
        String color,
        String size
    );

    boolean existsByProductIdAndProductColorNameIgnoreCaseAndSizeIsNull(
        Integer productId,
        String color
    );

    boolean existsByProductIdAndProductColorIsNullAndSizeIgnoreCase(
            Integer productId,
            String size
    );

    boolean existsByProductIdAndProductColorIsNullAndSizeIsNull(
            Integer productId
    );
   */
    List<Variant> findByProductId(Integer productId);

    List<Variant> findByProductIdIn(List<Integer> productIds);

    @Query("""
        SELECT v
        FROM Variant v
        LEFT JOIN FETCH v.stock
        WHERE v.product.id = :productId
    """)
    List<Variant> findByProductIdWithStock(
            @Param("productId") Integer productId
    );

    @Query("""
        SELECT v
        FROM Variant v
        LEFT JOIN FETCH v.stock
        WHERE v.product.id IN :productIds
    """)
    List<Variant> findByProductIdInWithStock(
            @Param("productIds") List<Integer> productIds
    );
}
