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
        p.imageUrl
    )
    from CatalogProduct p
    where p.productId in :ids
      and (:minPrice is null or p.price >= :minPrice)
      and (:maxPrice is null or p.price <= :maxPrice)
    order by p.name
    """)
    List<ProductCardDto> findCards(
            @Param("ids") List<Long> ids,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
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
    FROM product_categories pc
    WHERE pc.category_id IN (
        SELECT id FROM category_tree
    )
    """, nativeQuery = true)
    List<Long> findProductIdsByCategory(
            @Param("categoryId") Long categoryId
    );

    @Query("""
    SELECT p.productId
    FROM CatalogProduct p
    """)
    List<Long> findAllProductIds();

}