package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    List<Conversation> findByUserId(Integer userId);
}
