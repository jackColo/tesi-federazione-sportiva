package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.model.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * Entità che rappresenta un singolo messaggio scambiato all'interno di una sessione
 * di chat, mappata sulla collection MongoDB "chat_messages".
 * Ogni messaggio è immutabile e storicizzato.
 * Il campo chatUserId viene utilizzato per raggruppare tutti i messaggi relativi alle
 * sessioni di chat tra un determinato clubManager e la federazione (per semplicità nella
 * definizione dei permessi d'accesso, corrisponde all'ID del club manager coinvolto).
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
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