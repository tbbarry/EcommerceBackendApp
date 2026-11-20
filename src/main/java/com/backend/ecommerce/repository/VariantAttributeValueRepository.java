package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.VariantAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VariantAttributeValueRepository
        extends JpaRepository<VariantAttributeValue, Integer> {
    
    @Query("SELECT vav FROM VariantAttributeValue vav WHERE vav.variant.id = :variantId")
    List<VariantAttributeValue> findByVariantId(@Param("variantId") Integer variantId);

    @Query("SELECT vav FROM VariantAttributeValue vav JOIN FETCH vav.attributeValue av JOIN FETCH av.attribute WHERE vav.variant.product.id = :productId")
    List<VariantAttributeValue> findDetailedByProductId(@Param("productId") Integer productId);

    @Query("SELECT DISTINCT vav.variant.product.id FROM VariantAttributeValue vav WHERE vav.attributeValue.id = :valueId")
    List<Integer> findProductIdsByAttributeValueId(@Param("valueId") Integer valueId);

    @Query("SELECT DISTINCT vav.variant.product.id FROM VariantAttributeValue vav WHERE vav.attributeValue.attribute.id = :attributeId")
    List<Integer> findProductIdsByAttributeId(@Param("attributeId") Integer attributeId);
}