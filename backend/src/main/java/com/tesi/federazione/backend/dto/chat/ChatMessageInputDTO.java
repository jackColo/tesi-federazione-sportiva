package com.tesi.federazione.backend.dto.chat;

import lombok.Data;

/**
 * DTO di input per l'invio di un nuovo messaggio.
 * Contiene solo i dati essenziali: il mittente e il timestamp vengono
 * determinati dal server per garantire sicurezza e consistenza temporale.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class ChatMessageInputDTO {
    private String message;
    /**
     * ID del club manager associato alla chat, utilizzato come chiave per
     * raggruppare tutti i messaggi relativi alla chat.
     */
    private String chatUserId;
}
