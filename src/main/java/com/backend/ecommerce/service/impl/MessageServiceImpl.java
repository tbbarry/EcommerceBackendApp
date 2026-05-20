package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.MessageDto;
import com.backend.ecommerce.entity.Conversation;
import com.backend.ecommerce.entity.Message;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ConversationRepository;
import com.backend.ecommerce.repository.MessageRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> findAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isAdmin(auth)) {
            throw new AccessDeniedException("Only admin can access all messages");
        }

        return messageRepository.findAll().stream().map(this::toDto).toList();
    }

    // Quand on accède à un message, on le marque comme lu
    @Override
    public MessageDto findById(Integer id) {
        Message message = getEntityById(id);

        checkConversationAccess(message.getConversation());

        if (Boolean.FALSE.equals(message.getRead())) {
            message.setRead(true);
            messageRepository.save(message);
        }

        return toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> findByConversationId(Integer conversationId) {
        Conversation conversation = getConversationById(conversationId);
        checkConversationAccess(conversation);

        return messageRepository.findByConversationId(conversationId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MessageDto create(MessageDto dto) {
        Conversation conversation = getConversationById(dto.getConversationId());
        checkConversationAccess(conversation);

        Message entity = new Message();
        entity.setConversation(conversation);
        entity.setContent(dto.getContent());
        entity.setSendDate(LocalDateTime.now());
        entity.setRead(false);

        return toDto(messageRepository.save(entity));
    }

    @Override
    public MessageDto update(Integer id, MessageDto dto) {
        Message entity = getEntityById(id);
        checkConversationAccess(entity.getConversation());

        entity.setContent(dto.getContent());

        return toDto(messageRepository.save(entity));
    }

    @Override
    public MessageDto markAsRead(Integer id) {
        Message entity = getEntityById(id);
        checkConversationAccess(entity.getConversation());

        entity.setRead(true);

        return toDto(messageRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Message entity = getEntityById(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isAdmin(auth)) {
            throw new AccessDeniedException("Only admin can delete messages");
        }

        messageRepository.delete(entity);
    }

    private Message getEntityById(Integer id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
    }

    private Conversation getConversationById(Integer id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + id));
    }

    private MessageDto toDto(Message entity) {
        MessageDto dto = new MessageDto();
        dto.setId(entity.getId());
        dto.setConversationId(entity.getConversation() != null ? entity.getConversation().getId() : null);
        dto.setContent(entity.getContent());
        dto.setSendDate(entity.getSendDate());
        dto.setRead(entity.getRead());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private void checkConversationAccess(Conversation conversation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isAdmin(auth)) {
            return;
        }

        User currentUser = getCurrentUser(auth);
        Integer ownerId = conversation.getUser().getId();

        if (!currentUser.getId().equals(ownerId)) {
            throw new AccessDeniedException("You are not allowed to access this conversation");
        }
    }

    private User getCurrentUser(Authentication auth) {
        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}