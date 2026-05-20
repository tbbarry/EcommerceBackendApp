package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.MessageDto;

import java.util.List;

public interface MessageService extends CrudService<MessageDto, Integer> {
    List<MessageDto> findByConversationId(Integer conversationId);
    MessageDto markAsRead(Integer id);
}