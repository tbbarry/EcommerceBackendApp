package com.backend.ecommerce.repository;
import com.backend.ecommerce.dto.ProductCardDto;
import com.backend.ecommerce.dto.CatalogSearchRequest;
import org.springframework.data.domain.Page;
import java.util.List;
public interface CatalogProductSearchRepository {

    Page<ProductCardDto> search(
            CatalogSearchRequest request,
            List<Long> categoryIds
    );

    List<Long> findProductIdsForFilters(
        CatalogSearchRequest request,
        List<Long> categoryIds,
        boolean applyCategoryFilter
   );
}