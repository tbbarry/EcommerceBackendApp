package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ConversationDto;
import com.backend.ecommerce.entity.Conversation;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ConversationRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> findAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isAdmin(auth)) {
            return conversationRepository.findAll().stream().map(this::toDto).toList();
        }

        User currentUser = getCurrentUser(auth);
        return conversationRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDto findById(Integer id) {
        Conversation conversation = getEntityById(id);
        checkUserAccess(conversation.getUser().getId());
        return toDto(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> findByUserId(Integer userId) {
        checkUserAccess(userId);

        return conversationRepository.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ConversationDto create(ConversationDto dto) {
        checkUserAccess(dto.getUserId());

        Conversation entity = new Conversation();
        applyDtoToEntity(dto, entity);

        return toDto(conversationRepository.save(entity));
    }

    @Override
    public ConversationDto update(Integer id, ConversationDto dto) {
        Conversation entity = getEntityById(id);
        checkUserAccess(entity.getUser().getId());

        entity.setSubject(dto.getSubject());

        return toDto(conversationRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Conversation entity = getEntityById(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isAdmin(auth)) {
            throw new AccessDeniedException("Only admin can delete conversations");
        }

        conversationRepository.delete(entity);
    }

    private Conversation getEntityById(Integer id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + id));
    }

    private void applyDtoToEntity(ConversationDto dto, Conversation entity) {
        entity.setSubject(dto.getSubject());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        entity.setUser(user);
    }

    private ConversationDto toDto(Conversation entity) {
        ConversationDto dto = new ConversationDto();
        dto.setId(entity.getId());
        dto.setSubject(entity.getSubject());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private void checkUserAccess(Integer userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isAdmin(auth)) {
            return;
        }

        User currentUser = getCurrentUser(auth);

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
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
