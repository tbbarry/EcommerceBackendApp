package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.VariantAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;   
public interface VariantAttributeValueRepository
        extends JpaRepository<VariantAttributeValue, Integer> {
}