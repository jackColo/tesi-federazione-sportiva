package com.tesi.federazione.backend.service.impl.chat;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.ChatMessage;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.service.chat.ChatAssignmentManager;
import com.tesi.federazione.backend.service.chat.ChatMediator;
import com.tesi.federazione.backend.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Classe d'implementazione del mediatore tra:
 * - WebSocket
 * - CRUD Service
 * - Gestore dell'assegnazione delle chat per gli amministratori
 */
@Service
@RequiredArgsConstructor
public class ChatMediatorImpl implements ChatMediator {

    private final ChatAssignmentManager assignmentManager;
    private final ChatMessageService chatMessageService;
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

        // Chiamo l'assignmentManager per la verifica dell'assegnazione della chat all'amministratore della federazione.
        if (currentUser.getRole().equals(Role.FEDERATION_MANAGER)) {
            String assignedAdmin = assignmentManager.getCurrentAdminForClubManager(msgDTO.getChatUserId());

            if (!currentUser.getId().equals(assignedAdmin))
                throw new IllegalStateException("Admin non autorizzato a scrivere in questa chat senza presa in carico.");
        }

        // Chiamo il service CRUD per la gestione del salvataggio del messaggio a DB
        ChatMessage msg = chatMessageService.saveChatMessage(msgDTO, currentUser.getId(),  currentUser.getRole());

        // Instradamento del messaggio verso il canale corretto
        String topicDestinazione = "/topic/user/" + msgDTO.getChatUserId();
        messagingTemplate.convertAndSend(topicDestinazione, msg);
    }

    /**
     * Metodo che richiama il gestore della presa in carico della chat per gli amministratori della federazione
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @param adminId Id dell'amministratore che sta cercando di prendere in carico la chat
     */
    @Override
    public void takeCharge(String chatUserId, String adminId) {
        assignmentManager.assignChat(chatUserId, adminId);
    }

    /**
     * Metodo che richiama il gestore del rilascio della chat presa in carico da un amministratore della federazione
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @param adminId Id dell'amministratore che sta richiedendo il rilascio della chat
     */
    @Override
    public void releaseChat(String chatUserId, String adminId) {
        assignmentManager.releaseChat(chatUserId);
    }

}