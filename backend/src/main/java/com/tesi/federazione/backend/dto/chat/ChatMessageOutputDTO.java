package com.tesi.federazione.backend.dto.chat;

import com.tesi.federazione.backend.model.enums.Role;
import lombok.Data;

/**
 * Classe DTO per gli oggetti ChatMessage in uscita verso il client
 */
@Data
public class ChatMessageOutputDTO {
    private String id;
    private String chatUserId;
    private String senderId;
    private Role senderRole;
    private String content;
    private String timestamp;
}
