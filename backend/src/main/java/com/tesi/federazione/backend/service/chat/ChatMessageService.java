package com.tesi.federazione.backend.service.chat;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.dto.chat.ChatMessageOutputDTO;
import com.tesi.federazione.backend.model.ChatMessage;
import com.tesi.federazione.backend.model.enums.Role;

import java.util.List;

/**
 * Interfaccia tra Controller e implementazione dei service per i metodi che manipolano oggetti di tipo ChatMessage
 */
public interface ChatMessageService {

    /**
     * Firma del metodo per recuperare lo storico di tutti i messaggi inviati nella chat tra amministratori e
     * clubManager con id 'chatUserId'.
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @return List<ChatMessageOutputDTO> Lista ordinata dello storico dei messaggi appartenenti alla chat
     */
    List<ChatMessageOutputDTO> getAllChatMessages(String chatUserId);

    /**
     * Firma del metodo per salvare un nuovo messaggio a DB
     *
     * @param dto        Messaggio da salvare come oggetto di tipo ChatMessaggeInputDTO
     * @param senderId   Id del mittente del messaggio
     * @param senderRole Ruolo del mittente del messaggio
     * @return Oggetto ChatMessage salvato.
     */
    ChatMessage saveChatMessage(ChatMessageInputDTO dto, String senderId, Role senderRole);
}
