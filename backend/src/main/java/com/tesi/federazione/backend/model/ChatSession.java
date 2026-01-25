package com.tesi.federazione.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Entità che definisce lo stato di una conversazione tra un Club Manager e uno specifico
 * responsabile della federazione (Admin), mappata sulla collection MongoDB "chat_sessions".
 * Permette di tracciare se una conversazione è attiva o chiusa e quale amministratore
 * l'ha presa in carico.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class ChatSession {
    @Id
    private String id;
    private String clubManagerId;
    private String adminId;
    private boolean active;
}
