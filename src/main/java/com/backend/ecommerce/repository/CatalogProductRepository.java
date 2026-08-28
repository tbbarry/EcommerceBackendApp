package com.backend.ecommerce.repository;

import com.backend.ecommerce.dto.ProductCardDto;
import com.backend.ecommerce.entity.CatalogProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
@Repository
public interface CatalogProductRepository
        extends JpaRepository<CatalogProduct, Long> {



    @Query("""
    select new com.backend.ecommerce.dto.ProductCardDto(
        p.productId,
        p.name,
        p.slug,
        p.price,
        p.imageUrl,
        p.categoryName
    )
    from CatalogProduct p
    where p.productId in :productIds
    """)
    List<ProductCardDto> findCardsByIds(
            @Param("productIds") List<Long> productIds

    );

    @Query(value = """
    WITH RECURSIVE category_tree AS (
        SELECT id
        FROM categories
        WHERE id = :categoryId

        UNION ALL

        SELECT c.id
        FROM categories c
        INNER JOIN category_tree ct
            ON c.category_id = ct.id
    )
    SELECT DISTINCT pc.product_id
    FROM CatalogProduct pc
    WHERE (
        :categoryId IS NULL
        OR pc.categoryId IN (
            SELECT id FROM category_tree
        )
    )
    AND (:minPrice IS NULL OR pc.price >= :minPrice)
    AND (:maxPrice IS NULL OR pc.price <= :maxPrice)
    """, nativeQuery = true)
    List<Long> findProductIdsByCategoryAndPriceRange(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("""
    SELECT p.productId
    FROM CatalogProduct p
    """)
    List<Long> findAllProductIds();

}