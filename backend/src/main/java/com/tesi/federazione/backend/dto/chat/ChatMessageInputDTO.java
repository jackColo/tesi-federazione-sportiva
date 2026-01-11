package com.tesi.federazione.backend.dto.chat;

import lombok.Data;

/**
 * Classe DTO per gli oggetti ChatMessage in ingresso dal client
 */
@Data
public class ChatMessageInputDTO {
    private String message;
    private String chatUserId;
}
