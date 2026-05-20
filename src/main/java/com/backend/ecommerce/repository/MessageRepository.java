package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByConversationId(Integer conversationId);
}
