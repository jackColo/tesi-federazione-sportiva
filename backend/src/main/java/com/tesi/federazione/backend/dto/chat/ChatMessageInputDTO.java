package com.tesi.federazione.backend.dto.chat;

import lombok.Data;

/**
 * Data Transfer Object (DTO) per gli oggetti ChatMessage in ingresso dal client
 */
@Data
public class ChatMessageInputDTO {
    private String message;
    private String chatUserId;
}
