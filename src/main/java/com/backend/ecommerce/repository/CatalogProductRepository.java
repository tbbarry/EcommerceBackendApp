package com.backend.ecommerce.repository;

import com.backend.ecommerce.dto.ProductCardDto;
import com.backend.ecommerce.entity.CatalogProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
@Repository
public interface CatalogProductRepository
        extends JpaRepository<CatalogProduct, Long>, CatalogProductSearchRepository {



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
    @Query("""
    select new com.backend.ecommerce.dto.ProductCardDto(
        p.productId,
        p.name,
        p.slug,
        p.price,
        p.imageUrl,
        p.categoryName,
        p.categoryId
    )
    from CatalogProduct p
    where (:categoryIds is null or p.categoryId in :categoryIds)
    and (:minPrice is null or p.price >= :minPrice)
    and (:maxPrice is null or p.price <= :maxPrice)
    """)
    Page<ProductCardDto> findCardsProduct(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );


    @Query(value = """
    SELECT DISTINCT pc.product_id
    FROM catalog_product pc
    WHERE (
        :categoryIds IS NULL
        OR pc.category_id IN (:categoryIds)
    )
    AND (:minPrice IS NULL OR pc.price >= :minPrice)
    AND (:maxPrice IS NULL OR pc.price <= :maxPrice)
    """, nativeQuery = true)
    List<Long> findProductIdsByCategoriesAndPriceRange(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("""
    SELECT p.productId
    FROM CatalogProduct p
    """)
    List<Long> findAllProductIds();

}