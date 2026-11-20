package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.AttributeValueDto;
import com.backend.ecommerce.dto.AttributeValueCreateRequest;

import java.util.List;

public interface AttributeValueService extends CrudService<AttributeValueDto, Integer> {
    
    /**
     * Créer une valeur d'attribut
     */
    AttributeValueDto create(AttributeValueCreateRequest request);
    
    /**
     * Trouver toutes les valeurs d'un attribut
     */
    List<AttributeValueDto> findByAttributeId(Integer attributeId);
    
    /**
     * Supprimer une valeur d'attribut
     */
    void delete(Integer id);
}
