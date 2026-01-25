package com.tesi.federazione.backend.dto.chat;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO di riepilogo utilizzato nella dashboard della chat amministrativa che fornisce
 * una vista sintetica di tutte le conversazioni (sia attive che inattive).
 * Include campi calcolati dal server come "waitingForReply", che serve per evidenziare visivamente
 * agli amministratori le chat che necessitano di un intervento immediato.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class ChatSummaryDTO {
    private String chatUserId;
    private String clubManagerName;
    private LocalDateTime lastMessageTime;

    private String status;
    private String assignedAdminId;

    private boolean waitingForReply;
}
