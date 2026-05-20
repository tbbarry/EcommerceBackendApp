package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ConversationDto;

import java.util.List;

public interface ConversationService extends CrudService<ConversationDto, Integer> {
    List<ConversationDto> findByUserId(Integer userId);
}