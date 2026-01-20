package com.tesi.federazione.backend.dto.chat;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) che rappresenta il riepilogo di una sessione di chat.
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
