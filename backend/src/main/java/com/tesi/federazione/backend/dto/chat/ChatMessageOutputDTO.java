package com.tesi.federazione.backend.dto.chat;

import com.tesi.federazione.backend.model.enums.Role;
import lombok.Data;

/**
 * DTO di output per la visualizzazione dei messaggi nel client.
 * Rispetto al modello, il timestamp viene convertito in String in formato ISO,
 * per facilitare il parsing e la visualizzazione da parte del client.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
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
