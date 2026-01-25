package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.chat.ChatMessageOutputDTO;
import com.tesi.federazione.backend.model.ChatMessage;
import org.springframework.stereotype.Component;

/**
 * Classe utility per mappare i gli oggetti ChatMessage nei formati DTO
 */
@Component
public class ChatMessageMapper {

    /**
     * Metodo per mappare un messaggio da entità a DTO
     * @param msg Messaggio come oggetto di tipo ChatMessage
     * @return ChatMessageOutputDTO Messaggio come oggetto di tipo ChatMessageOutputDTO
     */
    public ChatMessageOutputDTO toDTO(ChatMessage msg) {
        if (msg == null) {
            return null;
        }

        ChatMessageOutputDTO dto = new ChatMessageOutputDTO();
        dto.setId(msg.getId());
        dto.setChatUserId(msg.getChatUserId());
        dto.setSenderId(msg.getSenderId());
        dto.setSenderRole(msg.getSenderRole());
        dto.setContent(msg.getContent());
        dto.setTimestamp(msg.getTimestamp().toString());

        return dto;
    }

}