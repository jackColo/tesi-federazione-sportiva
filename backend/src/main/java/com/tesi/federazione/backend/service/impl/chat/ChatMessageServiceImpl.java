package com.tesi.federazione.backend.service.impl.chat;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.dto.chat.ChatMessageOutputDTO;
import com.tesi.federazione.backend.mapper.ChatMessageMapper;
import com.tesi.federazione.backend.model.ChatMessage;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.ChatMessageRepository;
import com.tesi.federazione.backend.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;

    /**
     * Metodo per recuperare lo storico di tutti i messaggi inviati nella chat tra amministratori e
     * clubManager con id 'chatUserId'.
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @return List<ChatMessageOutputDTO> Lista ordinata dello storico dei messaggi appartenenti alla chat
     */
    @Override
    public List<ChatMessageOutputDTO> getAllChatMessages(String chatUserId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatUserIdOrderByTimestampAsc(chatUserId);
        return chatMessages.stream().map(chatMessageMapper::toDTO).toList();
    }

    /**
     * Metodo per salvare un nuovo messaggio a DB
     *
     * @param dto        Messaggio da salvare come oggetto di tipo ChatMessaggeInputDTO
     * @param senderId   Id del mittente del messaggio
     * @param senderRole Ruolo del mittente del messaggio
     * @return Oggetto ChatMessage salvato.
     */
    @Override
    public ChatMessage saveChatMessage(ChatMessageInputDTO dto, String senderId, Role senderRole) {
        ChatMessage msg = new ChatMessage();
        msg.setChatUserId(dto.getChatUserId());
        msg.setContent(dto.getMessage());
        msg.setSenderId(senderId);
        msg.setTimestamp(LocalDateTime.now());
        msg.setSenderRole(senderRole);

        return chatMessageRepository.save(msg);
    }
}
