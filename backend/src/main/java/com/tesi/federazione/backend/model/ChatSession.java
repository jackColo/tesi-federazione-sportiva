package com.tesi.federazione.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Classe di definizione degli oggetti che corrispondono alle sessioni delle chat, utilizzata per il salvataggio a DB.
 */
@Data
public class ChatSession {
    @Id
    private String id;
    private String clubManagerId;
    private String adminId;
    private boolean active;
}
