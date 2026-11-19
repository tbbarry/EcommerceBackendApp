package com.backend.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@Table(name = "catalog_product_facet")
public class CatalogProductFacet extends BaseEntity {


  
    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long facetId;

    @Column(nullable = false)
    private String facetCode;

    @Column(nullable = false)
    private Long facetValueId;

    @Column(nullable = false)
    private String valueCode;
    @Column(nullable = false)
    private String valueLabel;
}