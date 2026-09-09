package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CatalogResponse;
import com.backend.ecommerce.dto.CatalogSearchRequest;
import com.backend.ecommerce.dto.CategoryDto2;
import com.backend.ecommerce.dto.CategoryRow;
import com.backend.ecommerce.dto.FacetDto;
import com.backend.ecommerce.dto.FacetValueDto;
import com.backend.ecommerce.dto.FacetValueProjection;
import com.backend.ecommerce.dto.ProductCardDto;
import com.backend.ecommerce.repository.CatalogFacetRepository;
import com.backend.ecommerce.repository.CatalogProductRepository;
import com.backend.ecommerce.repository.CategoryRepository;
import com.backend.ecommerce.service.CatalogService2;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CatalogServiceImpl2 implements CatalogService2 {

    private final CatalogProductRepository catalogProductRepository;
    private final CatalogFacetRepository facetRepository;
    private final CategoryRepository categoryRepository;


    
    
public CatalogResponse search(CatalogSearchRequest request) {

    List<Long> categoryIds = null;

    if (request.getCategoryId() != null) {

        categoryIds = categoryRepository
                .findCategoryTreeIds(request.getCategoryId())
                .stream()
                .map(Long::valueOf)
                .toList();
    }

    Page<ProductCardDto> page =
            catalogProductRepository.search(
                    request,
                    categoryIds
            );

          // -----------------------------------------
    // 3. Tous les productIds correspondant
    //    aux filtres
    // -----------------------------------------

        List<Long> productIds =
                catalogProductRepository.findProductIdsForFilters(
                        request,
                        categoryIds,
                        true
                );

         List<FacetValueProjection> facetRows =
            facetRepository.findFacetsByProductIds(
                    productIds
            );

        List<FacetDto> facets =
                buildFacets(
                        facetRows,
                        request
                );

        List<Long> productIdsForCategories = 
                catalogProductRepository.findProductIdsForFilters(
                        request,categoryIds,
                        true);

        List <Long> categoryIdsForProducts = 
                catalogProductRepository.findCategoryIdsByProductIds(
                        productIdsForCategories
                );
        List<CategoryRow> categoryRows = 
                categoryRepository.findCategoriesAndParents(categoryIdsForProducts);
        
        List<CategoryDto2> categories = buildCategories(categoryRows);


    return new CatalogResponse(
            page.getContent(),
            categories,
            facets,
            page.getTotalElements(),
            page.getNumber(),
            page.getSize()
    );
}


   private List<FacetDto> buildFacets(List<FacetValueProjection> rows, CatalogSearchRequest request) {

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


   private List<CategoryDto2> buildCategories(List<CategoryRow> rows) {

    Map<Long, Node> nodes = new HashMap<>();
    List<Node> roots = new ArrayList<>();

    // Création des nœuds
    for (CategoryRow row : rows) {
        nodes.put(
            row.getId(),
            new Node(row.getId(), row.getName(), row.getParentId())
        );
    }

    // Construction de l'arbre
    for (Node node : nodes.values()) {

        if (node.parentId == null) {
            roots.add(node);
            continue;
        }

        Node parent = nodes.get(node.parentId);

        if (parent != null) {
            parent.children.add(node);
        } else {
            roots.add(node);
        }
    }

    // Conversion vers les DTO immutables
    return roots.stream()
            .map(this::toDto)
            .toList();
}

private CategoryDto2 toDto(Node node) {
        return new CategoryDto2(
                node.id,
                node.name,
                node.children.stream()
                        .map(this::toDto)
                        .toList()
        );
        }

private static class Node {

    Long id;
    String name;
    Long parentId;
    List<Node> children = new ArrayList<>();

    Node(Long id, String name, Long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }
}       
/* 
   private Set<Long> applyFacetFilters(Set<Long> productIds, List<FacetFilter> facets) {

    for (FacetFilter facet : facets) {

        productIds.retainAll(
                facetRepository.findProductIdsByFacet(
                        facet.getFacetId(),
                        facet.getValueIds()
                )
        );

        if (productIds.isEmpty()) {
            break;
        }
    }

    return productIds;
}

private List<Long> findProductIds(CatalogSearchRequest request) {

    Set<Long> productIds = new HashSet<>(
            productRepository.findProductIdsByCategoryAndPriceRange(
                    request.getCategoryId(),
                    request.getMinPrice(),
                    request.getMaxPrice()
            )
    );

    applyFacetFilters(productIds, request.getFacets());

    return new ArrayList<>(productIds);
}
        

 
    private List<CategoryDto2> buildCategories() {

        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNullOrderByName();

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

*/
}