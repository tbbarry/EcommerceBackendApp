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
public class MessageDto {
    private Integer id;
    private Integer conversationId;
    private String content;
    private LocalDateTime sendDate;
    private Boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}