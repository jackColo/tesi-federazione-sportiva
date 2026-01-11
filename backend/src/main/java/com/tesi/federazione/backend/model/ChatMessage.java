package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.model.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * Classe di definizione degli oggetti che corrispondono ai messaggi delle chat, utilizzata per il salvataggio a DB.
 */
@Data
public class ChatMessage {
    @Id
    private String id;
    private String chatUserId;
    private String senderId;
    private Role senderRole;
    private String content;
    private LocalDateTime timestamp;
}