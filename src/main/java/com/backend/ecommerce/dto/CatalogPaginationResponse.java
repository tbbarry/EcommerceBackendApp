package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogPaginationResponse {
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
    private boolean hasNext;
}
