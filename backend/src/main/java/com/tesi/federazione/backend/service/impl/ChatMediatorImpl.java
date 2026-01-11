package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.dto.chat.ChatMessageOutputDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.mapper.ChatMessageMapper;
import com.tesi.federazione.backend.model.ChatMessage;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.ChatMessageRepository;
import com.tesi.federazione.backend.service.ChatMediator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe d'implementazione del mediatore tra WebSocket - DB - Gestore dell'assegnazione delle chat per gli amministratori
 */
@Service
@RequiredArgsConstructor
public class ChatMediatorImpl implements ChatMediator {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;

    //private final ChatAssignmentManager assignmentManager;

    private final SimpMessagingTemplate messagingTemplate;

    // Metodi per il protocollo STOMP del WebSocket

    /**
     * Implementazione del metoodo per instradare il messaggio nel canale corretto ed effettuare le opportune verifiche
     * sui permessi d'accesso in base al ruolo dell'utente.
     *
     * @param msgDTO Messaggio in input
     * @param currentUser Utente che sta inviando il messaggio
     */
    @Override
    public void routeMessage(ChatMessageInputDTO msgDTO, User currentUser) {

        // Controllo dei permessi d'accesso
        if ((currentUser.getRole().equals(Role.CLUB_MANAGER) && !currentUser.getId().equals(msgDTO.getChatUserId())) || currentUser.getRole().equals(Role.ATHLETE)) {
            throw new ActionNotAllowedException("You are not allowed to write in this chat!");
        }

        // Verifica dell'assegnazione della chat per gli amministratori della federazione.
        if (currentUser.getRole().equals(Role.FEDERATION_MANAGER)) {
            //String assignedAdmin = assignmentManager.getAssignedAdmin(msg.getClubId());

            //if (!msg.getSenderId().equals(assignedAdmin))
            //    throw new IllegalStateException("Admin non autorizzato a scrivere in questa chat senza presa in carico.");
        }

        // Costruzione e salvataggio del messaggio a DB
        ChatMessage msg = new ChatMessage();
        msg.setChatUserId(msgDTO.getChatUserId());
        msg.setContent(msgDTO.getMessage());
        msg.setSenderId(currentUser.getId());
        msg.setTimestamp(LocalDateTime.now());
        msg.setSenderRole(currentUser.getRole());

        ChatMessage savedMsg = chatMessageRepository.save(msg);
        msg.setId(savedMsg.getId());

        // Instradamento del messaggio verso il canale corretto
        String topicDestinazione = "/topic/user/" + msg.getChatUserId();
        messagingTemplate.convertAndSend(topicDestinazione, msg);
    }

    /**
     * Metodo che richiama il gestore della presa in carico della chat per gli amministratori della federazione
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @param adminId Id dell'amministratore che sta cercando di prendere in carico la chat
     * @return boolean Booleano che indica il permesso concesso o negato
     */
    @Override
    public boolean takeCharge(String chatUserId, String adminId) {
        //return assignmentManager.assignChat(clubId, adminId);
        return true;
    }

    /**
     * Metodo che richiama il gestore del rilascio della chat presa in carico da un amministratore della federazione
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @param adminId Id dell'amministratore che sta richiedendo il rilascio della chat
     */
    @Override
    public void releaseChat(String chatUserId, String adminId) {
        //assignmentManager.releaseChat(clubId, adminId);
    }

    /**
     * Metodo per recuperare l'elenco di tutti i messaggi inviati nella chat tra amministratori e
     * clubManager con id 'clubUserId' utilizzato alla riapertura della connessione WebSocket.
     *
     * @param clubUserId Id della chat tra amministratori e clubManager
     * @return List<ChatMessageOutputDTO> Lista ordinata dello storico dei messaggi appartenenti alla chat
     */
    @Override
    public List<ChatMessageOutputDTO> getAllChatMessages(String clubUserId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatUserIdOrderByTimestampAsc(clubUserId);
        return chatMessages.stream().map(chatMessageMapper::toDTO).toList();
    }

}