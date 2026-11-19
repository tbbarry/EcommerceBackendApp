package com.backend.ecommerce.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/catalog/products2")
@RequiredArgsConstructor
public class CatalogController {
    /* 
    private final CatalogService catalogService;

    @GetMapping
    public ResponseEntity<CatalogSearchResponse> search(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "24") Integer size,
            @RequestParam(defaultValue = "relevance") String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) List<String> colors,
            @RequestParam(required = false) List<String> sizes
    ) {
        CatalogSearchRequest request = new CatalogSearchRequest();
        request.setPage(page);
        request.setSize(size);
        request.setSort(sort);
        request.setQuery(q);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setCategories(categories == null ? List.of() : categories);
        request.setColors(colors == null ? List.of() : colors);
        request.setSizes(sizes == null ? List.of() : sizes);

        return ResponseEntity.ok(catalogService.searchProducts(request));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CatalogProductDetailResponse> details(@PathVariable String slug) {
        return ResponseEntity.ok(catalogService.getProductDetailsBySlug(slug));
    }
    */ 
}
