package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDto {
    private Integer id;
    private Integer userId;
    private String subject;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
