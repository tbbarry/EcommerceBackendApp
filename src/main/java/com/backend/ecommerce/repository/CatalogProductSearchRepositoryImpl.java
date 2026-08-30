package com.backend.ecommerce.repository;

import com.backend.ecommerce.dto.CatalogSearchRequest;
import com.backend.ecommerce.dto.FacetFilter;
import com.backend.ecommerce.dto.ProductCardDto;
import com.backend.ecommerce.entity.CatalogProduct;
import com.backend.ecommerce.entity.CatalogProductFacet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CatalogProductSearchRepositoryImpl
        implements CatalogProductSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ProductCardDto> search(
            CatalogSearchRequest request,
            List<Long> categoryIds
    ) {

        CriteriaBuilder cb =
                entityManager.getCriteriaBuilder();


        // =====================================================
        // 1. REQUÊTE PRODUITS
        // =====================================================

        CriteriaQuery<ProductCardDto> query =
                cb.createQuery(ProductCardDto.class);

        Root<CatalogProduct> product =
                query.from(CatalogProduct.class);


        // =====================================================
        // 2. PROJECTION
        // =====================================================

        query.select(
                cb.construct(
                        ProductCardDto.class,
                        product.get("productId"),
                        product.get("name"),
                        product.get("slug"),
                        product.get("price"),
                        product.get("imageUrl"),
                        product.get("categoryName")
                )
        );


        // =====================================================
        // 3. PREDICATES PRODUITS
        // =====================================================

        List<Predicate> productPredicates =
                new ArrayList<>();


        // -----------------------------------------------------
        // Catégorie
        // -----------------------------------------------------

        addCategoryPredicate(
                productPredicates,
                product,
                categoryIds
        );


        // -----------------------------------------------------
        // Prix minimum
        // -----------------------------------------------------

        if (request.getMinPrice() != null) {

            productPredicates.add(
                    cb.greaterThanOrEqualTo(
                            product.get("price"),
                            request.getMinPrice()
                    )
            );
        }


        // -----------------------------------------------------
        // Prix maximum
        // -----------------------------------------------------

        if (request.getMaxPrice() != null) {

            productPredicates.add(
                    cb.lessThanOrEqualTo(
                            product.get("price"),
                            request.getMaxPrice()
                    )
            );
        }


        // -----------------------------------------------------
        // Facettes
        // -----------------------------------------------------

        addFacetPredicates(
                query,
                cb,
                product,
                productPredicates,
                request.getFacets()
        );


        // =====================================================
        // 4. APPLICATION DES FILTRES
        // =====================================================

        if (!productPredicates.isEmpty()) {

            query.where(
                    cb.and(
                            productPredicates.toArray(
                                    new Predicate[0]
                            )
                    )
            );
        }


        // =====================================================
        // 5. PAGINATION
        // =====================================================

        TypedQuery<ProductCardDto> typedQuery =
                entityManager.createQuery(query);

        int page = request.getPage();
        int size = request.getSize();

        typedQuery.setFirstResult(
                page * size
        );

        typedQuery.setMaxResults(size);


        List<ProductCardDto> products =
                typedQuery.getResultList();


        // =====================================================
        // 6. REQUÊTE COUNT
        // =====================================================

        CriteriaQuery<Long> countQuery =
                cb.createQuery(Long.class);

        Root<CatalogProduct> countProduct =
                countQuery.from(CatalogProduct.class);

        countQuery.select(
                cb.count(countProduct)
        );


        // =====================================================
        // 7. PREDICATES COUNT
        // =====================================================

        List<Predicate> countPredicates =
                new ArrayList<>();


        // -----------------------------------------------------
        // Catégorie
        // -----------------------------------------------------

        addCategoryPredicate(
                countPredicates,
                countProduct,
                categoryIds
        );


        // -----------------------------------------------------
        // Prix minimum
        // -----------------------------------------------------

        if (request.getMinPrice() != null) {

            countPredicates.add(
                    cb.greaterThanOrEqualTo(
                            countProduct.get("price"),
                            request.getMinPrice()
                    )
            );
        }


        // -----------------------------------------------------
        // Prix maximum
        // -----------------------------------------------------

        if (request.getMaxPrice() != null) {

            countPredicates.add(
                    cb.lessThanOrEqualTo(
                            countProduct.get("price"),
                            request.getMaxPrice()
                    )
            );
        }


        // -----------------------------------------------------
        // Facettes
        // -----------------------------------------------------

        addFacetPredicates(
                countQuery,
                cb,
                countProduct,
                countPredicates,
                request.getFacets()
        );


        // =====================================================
        // 8. APPLICATION DES FILTRES AU COUNT
        // =====================================================

        if (!countPredicates.isEmpty()) {

            countQuery.where(
                    cb.and(
                            countPredicates.toArray(
                                    new Predicate[0]
                            )
                    )
            );
        }


        // =====================================================
        // 9. EXÉCUTION DU COUNT
        // =====================================================

        Long total =
                entityManager
                        .createQuery(countQuery)
                        .getSingleResult();


        // =====================================================
        // 10. CONSTRUCTION DE LA PAGE
        // =====================================================

        Pageable pageable =
                PageRequest.of(page, size);

        return new PageImpl<>(
                products,
                pageable,
                total
        );
    }


    // =========================================================
    // CATEGORY
    // =========================================================

    private void addCategoryPredicate(
            List<Predicate> predicates,
            Root<CatalogProduct> product,
            List<Long> categoryIds
    ) {

        if (categoryIds == null ||
                categoryIds.isEmpty()) {

            return;
        }

        predicates.add(
                product.get("categoryId")
                        .in(categoryIds)
        );
    }


    // =========================================================
    // FACETS
    // =========================================================

    private void addFacetPredicates(
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            Root<CatalogProduct> product,
            List<Predicate> predicates,
            List<FacetFilter> facets
    ) {

        if (facets == null || facets.isEmpty()) {
            return;
        }


        for (FacetFilter facetFilter : facets) {

            if (facetFilter.getFacetId() == null ||
                    facetFilter.getValueIds() == null ||
                    facetFilter.getValueIds().isEmpty()) {

                continue;
            }


            // -------------------------------------------------
            // Sous-requête EXISTS
            // -------------------------------------------------

            Subquery<Long> subquery =
                    query.subquery(Long.class);


            Root<CatalogProductFacet> facet =
                    subquery.from(
                            CatalogProductFacet.class
                    );


            // SELECT 1

            subquery.select(
                    cb.literal(1L)
            );


            // -------------------------------------------------
            // Conditions du EXISTS
            // -------------------------------------------------

            Predicate sameProduct =
                    cb.equal(
                            facet.get("productId"),
                            product.get("productId")
                    );


            Predicate sameFacet =
                    cb.equal(
                            facet.get("facetId"),
                            facetFilter.getFacetId()
                    );


            Predicate valueSelected =
                    facet.get("facetValueId")
                            .in(
                                    facetFilter.getValueIds()
                            );


            // -------------------------------------------------
            // WHERE du EXISTS
            // -------------------------------------------------

            subquery.where(
                    cb.and(
                            sameProduct,
                            sameFacet,
                            valueSelected
                    )
            );


            // -------------------------------------------------
            // EXISTS ajouté aux filtres principaux
            // -------------------------------------------------

            predicates.add(
                    cb.exists(subquery)
            );
        }
    }


    @Override
public List<Long> findProductIdsForFilters(
        CatalogSearchRequest request,
        List<Long> categoryIds
) {

    CriteriaBuilder cb =
            entityManager.getCriteriaBuilder();

    CriteriaQuery<Long> query =
            cb.createQuery(Long.class);

    Root<CatalogProduct> product =
            query.from(CatalogProduct.class);


    // =====================================================
    // SELECT productId
    // =====================================================

    query.select(
            product.get("productId")
    );


    // =====================================================
    // PREDICATES
    // =====================================================

    List<Predicate> predicates =
            new ArrayList<>();


    // -----------------------------------------------------
    // Catégorie
    // -----------------------------------------------------

    addCategoryPredicate(
            predicates,
            product,
            categoryIds
    );


    // -----------------------------------------------------
    // Prix minimum
    // -----------------------------------------------------

    if (request.getMinPrice() != null) {

        predicates.add(
                cb.greaterThanOrEqualTo(
                        product.get("price"),
                        request.getMinPrice()
                )
        );
    }


    // -----------------------------------------------------
    // Prix maximum
    // -----------------------------------------------------

    if (request.getMaxPrice() != null) {

        predicates.add(
                cb.lessThanOrEqualTo(
                        product.get("price"),
                        request.getMaxPrice()
                )
        );
    }


    // -----------------------------------------------------
    // Facettes
    // -----------------------------------------------------

    addFacetPredicates(
            query,
            cb,
            product,
            predicates,
            request.getFacets()
    );


    // =====================================================
    // WHERE
    // =====================================================

    if (!predicates.isEmpty()) {

        query.where(
                cb.and(
                        predicates.toArray(
                                new Predicate[0]
                        )
                )
        );
    }


    // =====================================================
    // EXECUTION
    // =====================================================

    return entityManager
            .createQuery(query)
            .getResultList();
}
}