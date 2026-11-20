package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttributeValueRepository
        extends JpaRepository<AttributeValue, Integer> {
    
    @Query("SELECT av FROM AttributeValue av WHERE av.attribute.id = :attributeId")
    List<AttributeValue> findByAttributeId(@Param("attributeId") Integer attributeId);
}