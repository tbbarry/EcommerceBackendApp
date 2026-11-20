package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.AttributeDto;
import com.backend.ecommerce.dto.AttributeCreateRequest;
import com.backend.ecommerce.dto.AttributeWithValuesDto;

import java.util.List;

public interface AttributeService extends CrudService<AttributeDto, Integer> {
    
    /**
     * Créer un attribut
     */
    AttributeDto create(AttributeCreateRequest request);
    
    /**
     * Récupérer tous les attributs avec leurs valeurs
     */
    List<AttributeDto> findAllWithValues();
    
    /**
     * Récupérer tous les attributs avec leurs valeurs (DTO optimisée pour le front)
     */
    List<AttributeWithValuesDto> getAllAttributesWithValues();
}
