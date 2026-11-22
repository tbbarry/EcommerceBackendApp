package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CatalogSearchRequest;
import com.backend.ecommerce.dto.CategoryRow;
import com.backend.ecommerce.repository.CatalogFacetRepository;
import com.backend.ecommerce.repository.CatalogProductRepository;
import com.backend.ecommerce.repository.CategoryRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogCategoryOrderTest {
    @Mock private CatalogProductRepository products;
    @Mock private CatalogFacetRepository facets;
    @Mock private CategoryRepository categories;
    @Mock private CatalogProductPricingIndexService pricing;

    @Test
    void catalogRespectsRootOrderAndSortsChildrenByName() {
        CatalogServiceImpl2 service = new CatalogServiceImpl2(products, facets, categories, pricing);
        when(products.search(any(), isNull())).thenReturn(Page.empty());
        when(products.findProductIdsForFilters(any(), isNull(), eq(true))).thenReturn(List.of(1L));
        when(products.findCategoryIdsByProductIds(List.of(1L))).thenReturn(List.of(1L, 2L));
        List<CategoryRow> rows = List.of(
                row(1L, "Homme", null, 2), row(2L, "Femme", null, 1),
                row(3L, "Robes", 2L, 0), row(4L, "Chaussures", 2L, 0));
        when(categories.findCategoriesAndParents(List.of(1L, 2L))).thenReturn(rows);

        var result = service.search(new CatalogSearchRequest());

        assertEquals(List.of("Femme", "Homme"), result.categories().stream().map(item -> item.name()).toList());
        assertEquals(List.of("Chaussures", "Robes"), result.categories().get(0).children().stream().map(item -> item.name()).toList());
    }

    private CategoryRow row(Long id, String name, Long parentId, int order) {
        CategoryRow row = mock(CategoryRow.class);
        when(row.getId()).thenReturn(id);
        when(row.getName()).thenReturn(name);
        when(row.getParentId()).thenReturn(parentId);
        when(row.getDisplayOrder()).thenReturn(order);
        return row;
    }
}
