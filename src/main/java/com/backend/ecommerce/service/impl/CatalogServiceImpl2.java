package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CatalogResponse;
import com.backend.ecommerce.dto.CatalogSearchRequest;
import com.backend.ecommerce.dto.CategoryDto2;
import com.backend.ecommerce.dto.FacetDto;
import com.backend.ecommerce.dto.FacetFilter;
import com.backend.ecommerce.dto.FacetValueDto;
import com.backend.ecommerce.dto.FacetValueProjection;
import com.backend.ecommerce.dto.ProductCardDto;
import com.backend.ecommerce.entity.Category;
import com.backend.ecommerce.repository.CatalogFacetRepository;
import com.backend.ecommerce.repository.CatalogProductRepository;
import com.backend.ecommerce.repository.CategoryRepository;
import com.backend.ecommerce.service.CatalogService2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl2 implements CatalogService2 {

    private final CatalogProductRepository productRepository;
    private final CatalogFacetRepository facetRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public CatalogResponse search(CatalogSearchRequest request) {

        // ============================================================
        // 1. Recherche des produits
        // ============================================================

        List<Long> productIds = findProductIds(request);
        List<ProductCardDto> products = productRepository.findCards(productIds,request.getMinPrice(),request.getMaxPrice());
        List<FacetValueProjection> facetValues = facetRepository.findFacetsByProductIds(productIds);
        List<FacetDto> facets = buildFacets(facetValues, request);
        List<CategoryDto2> categories = buildCategories();

        return new CatalogResponse(products, categories, facets, productIds.size(), request.getPage(), request.getSize());
    }

private List<FacetDto> buildFacets(
        List<FacetValueProjection> rows,
        CatalogSearchRequest request
) {

    Map<Long, List<FacetValueDto>> values = new LinkedHashMap<>();
    Map<Long, String> codes = new LinkedHashMap<>();

    for (FacetValueProjection row : rows) {

        codes.putIfAbsent(row.facetId(), row.facetCode());

        boolean selected = request.getFacets().stream()
                .anyMatch(f ->
                        f.getFacetId().equals(row.facetId())
                        && f.getValueIds().contains(row.valueId())
                );

        values.computeIfAbsent(row.facetId(), k -> new ArrayList<>())
                .add(new FacetValueDto(
                        row.valueId(),
                        row.valueLabel(),
                        selected
                ));
    }

    return values.entrySet().stream()
            .map(e -> new FacetDto(
                    e.getKey(),
                    codes.get(e.getKey()),
                    e.getValue()
            ))
            .toList();
}
    
private List<Long> findProductIds(CatalogSearchRequest request) {

        List<Long> productIds;

        if (request.getCategoryId() != null) {

                productIds = new ArrayList<>(
                        productRepository.findProductIdsByCategory(
                                request.getCategoryId()
                        )
                );

        } else {

                productIds = new ArrayList<>(
                        productRepository.findAllProductIds()
                );
        }

        System.out.println("CATEGORY PRODUCT IDS = " + productIds);

        // Ensuite seulement : facettes
        for (FacetFilter facet : request.getFacets()) {
                System.out.println(
                "FACET ID = " + facet.getFacetId()
                + " VALUES = " + facet.getValueIds()
                );

                List<Long> facetProductIds = facetRepository.findProductIdsByFacet(facet.getFacetId(),facet.getValueIds());

                        
                System.out.println(
                        "FACET PRODUCT IDS = " + facetProductIds
                );
                productIds.retainAll(facetProductIds);

                System.out.println(
                "AFTER INTERSECTION = " + productIds
                );

                if (productIds.isEmpty()) {
                break;
                }
        }

    return productIds;
}
        private List<CategoryDto2> buildCategories() {

        List<Category> rootCategories =
                categoryRepository.findByParentCategoryIsNullOrderByName();

        return rootCategories.stream()
                .map(this::toCategoryDto)
                .toList();
        }


private CategoryDto2 toCategoryDto(Category category) {

    List<CategoryDto2> children =
            categoryRepository
                    .findByParentCategoryIdOrderByName(category.getId())
                    .stream()
                    .map(this::toCategoryDto)
                    .toList();

    return new CategoryDto2(
            Long.valueOf(category.getId()),
            category.getName(),
            children
    );
}
}