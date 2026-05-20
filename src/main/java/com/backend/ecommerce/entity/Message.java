package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime sendDate;

    @Column(name = "isRead")
    private Boolean read;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversationId", nullable = false)
    private Conversation conversation;
}
