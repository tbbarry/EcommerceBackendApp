package com.backend.ecommerce.specification;

import com.backend.ecommerce.entity.CatalogProduct;
import com.backend.ecommerce.entity.CatalogProductFacet;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import com.backend.ecommerce.dto.CatalogSearchRequest;
import com.backend.ecommerce.dto.FacetFilter;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
public final class CatalogProductSpecification {

    private CatalogProductSpecification() {
    }

    public static Specification<CatalogProduct> build(CatalogSearchRequest request,List<Long> categoryIds
    ) {

        Specification<CatalogProduct> specification = Specification.where(null);

        /*
         * CATEGORY
         */
        if (categoryIds != null && !categoryIds.isEmpty()) {

            specification = specification.and(
                    (root, query, cb) ->
                            root.get("categoryId").in(categoryIds)
            );
        }

        /*
         * MIN PRICE
         */
        if (request.getMinPrice() != null) {

            specification = specification.and(
                    (root, query, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.get("price"),
                                    request.getMinPrice()
                            )
            );
        }

        /*
         * MAX PRICE
         */
        if (request.getMaxPrice() != null) {

            specification = specification.and(
                    (root, query, cb) ->
                            cb.lessThanOrEqualTo(
                                    root.get("price"),
                                    request.getMaxPrice()
                            )
            );
        }

        /*
         * FACETS
         */
        if (request.getFacets() != null) {

            for (FacetFilter facet : request.getFacets()) {

                if (facet.getFacetId() == null
                        || facet.getValueIds() == null
                        || facet.getValueIds().isEmpty()) {
                    continue;
                }

                specification = specification.and(
                        facetSpecification(
                                facet.getFacetId(),
                                facet.getValueIds()
                        )
                );
            }
        }

        return specification;
    }


    private static Specification<CatalogProduct> facetSpecification(
            Long facetId,
            List<Long> valueIds
    ) {

        return (root, query, cb) -> {

            Subquery<Long> subquery =
                    query.subquery(Long.class);

            Root<CatalogProductFacet> facetRoot =
                    subquery.from(CatalogProductFacet.class);

            subquery.select(facetRoot.get("productId"));

            subquery.where(
                    cb.equal(
                            facetRoot.get("productId"),
                            root.get("productId")
                    ),

                    cb.equal(
                            facetRoot.get("facetId"),
                            facetId
                    ),

                    facetRoot.get("facetValueId").in(valueIds)
            );

            return cb.exists(subquery);
        };
    }
}