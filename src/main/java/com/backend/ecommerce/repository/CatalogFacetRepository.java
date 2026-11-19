package com.backend.ecommerce.repository;

import com.backend.ecommerce.dto.FacetValueProjection;
import com.backend.ecommerce.entity.CatalogProductFacet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogFacetRepository
        extends JpaRepository<CatalogProductFacet, Long> {

    @Query("""
        SELECT DISTINCT f.productId
        FROM CatalogProductFacet f
        WHERE f.facetId = :facetId
        AND f.facetValueId IN :valueIds
    """)
    List<Long> findProductIdsByFacet(
            @Param("facetId") Long facetId,
            @Param("valueIds") List<Long> valueIds
    );

    @Query("""
    SELECT DISTINCT new com.backend.ecommerce.dto.FacetValueProjection(
        f.facetId,
        f.facetCode,
        f.facetValueId,
        av.value
    )
    FROM CatalogProductFacet f
    JOIN AttributeValue av
        ON av.id = f.facetValueId
    WHERE f.productId IN :productIds
    ORDER BY f.facetId, av.value
    """)
    List<FacetValueProjection> findFacetsByProductIds(
            @Param("productIds") List<Long> productIds
    );
}