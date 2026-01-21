package com.tesi.federazione.backend.service.impl.chat;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.dto.chat.ChatMessageOutputDTO;
import com.tesi.federazione.backend.dto.chat.ChatSummaryDTO;
import com.tesi.federazione.backend.mapper.ChatMessageMapper;
import com.tesi.federazione.backend.model.ChatMessage;
import com.tesi.federazione.backend.model.ChatSession;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.ChatMessageRepository;
import com.tesi.federazione.backend.repository.ChatSessionRepository;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione del servizio ChatMessageService, necessario alla manipolazione a DB degli oggetti di tipo ChatMessage
 */
@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final UserRepository userRepository;
    private final ChatSessionRepository chatSessionRepository;
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

    /**
     * Metodo per recuperare l'elenco sommario di tutte le chat tra federazione e club manager.
     * @return List<ChatSummaryDTO> Elenco delle chat con formato ChatSummaryDTO
     */
    @Override
    public List<ChatSummaryDTO> getChatSummaries() {
        List<User> clubManagers = userRepository.findByRole(Role.CLUB_MANAGER);

        List<ChatSummaryDTO> summaries = new ArrayList<>();

        // Per ogni club manager verifico se ho una sessione attiva e recupero l'ultimo messaggio
        // per determinare se il club manager sia in attesa di una risposta
        for (User manager : clubManagers) {

            Optional<ChatSession> activeSession = chatSessionRepository
                    .findByClubManagerIdAndActiveTrue(manager.getId());

            List<ChatMessage> lastMsgs = chatMessageRepository.findByChatUserIdOrderByTimestampDesc(manager.getId(), PageRequest.of(0, 1));

            ChatMessage lastMsg = lastMsgs.isEmpty() ? null : lastMsgs.get(0);

            boolean isWaiting = lastMsg != null && lastMsg.getSenderId().equals(manager.getId());

            ChatSummaryDTO dto = new ChatSummaryDTO();
            dto.setChatUserId(manager.getId());
            dto.setClubManagerName(manager.getFirstName() + " " + manager.getLastName());
            dto.setStatus(activeSession.isPresent() ? "ASSIGNED" : "FREE");
            dto.setAssignedAdminId(activeSession.map(ChatSession::getAdminId).orElse(null));
            dto.setLastMessageTime(lastMsg != null ? lastMsg.getTimestamp() : null);
            dto.setWaitingForReply(isWaiting);

            summaries.add(dto);
        }

        // Ordino l'elenco delle chat mettendo prima quelle in attesa per darne maggiore visibilità agli amministratori
        summaries.sort((a, b) -> {
            if (a.isWaitingForReply() != b.isWaitingForReply()) {
                return a.isWaitingForReply() ? -1 : 1;
            }
            if (a.getLastMessageTime() == null) return 1;
            if (b.getLastMessageTime() == null) return -1;
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });

        return summaries;
    }
}
