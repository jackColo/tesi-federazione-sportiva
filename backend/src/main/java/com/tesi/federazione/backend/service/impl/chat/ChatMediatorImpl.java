package com.tesi.federazione.backend.service.impl.chat;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.ChatMessage;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.service.chat.ChatMediator;
import com.tesi.federazione.backend.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ChatMediatorImpl implements ChatMediator {

    private final ChatAssignmentManager assignmentManager;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    // Metodi per il protocollo STOMP del WebSocket

    /**
     * Implementazione del metodo per instradare il messaggio nel canale corretto ed effettuare le opportune verifiche
     * sui permessi d'accesso in base al ruolo dell'utente.
     *
     * @param msgDTO      Messaggio in ingresso
     * @param currentUser Utente che sta inviando il messaggio
     * @throws ActionNotAllowedException Se l'utente non ha i permessi sulla chat o se l'amministratore non ha preso in carico la conversazione.
     */
    @Override
    public void routeMessage(ChatMessageInputDTO msgDTO, User currentUser) {

        // Controllo dei permessi d'accesso
        if ((currentUser.getRole().equals(Role.CLUB_MANAGER) && !currentUser.getId().equals(msgDTO.getChatUserId())) || currentUser.getRole().equals(Role.ATHLETE)) {
            log.error("L'utente non ha i permessi per scrivere sulla chat indicata");
            throw new ActionNotAllowedException("Non hai i permessi per scrivere in questa chat!");
        }

        // Chiamo l'assignmentManager per la verifica dell'assegnazione della chat all'amministratore della federazione.
        if (currentUser.getRole().equals(Role.FEDERATION_MANAGER)) {
            String assignedAdmin = assignmentManager.getCurrentAdminForClubManager(msgDTO.getChatUserId());

            if (!currentUser.getId().equals(assignedAdmin)) {
                log.error("L'amministratore non può inviare messaggi perchè non ha preso in carico la chat");
                throw new ActionNotAllowedException("Admin non autorizzato a scrivere in questa chat senza presa in carico.");
            }
        }

        // Chiamo il service CRUD per la gestione del salvataggio del messaggio a DB
        ChatMessage msg = chatMessageService.saveChatMessage(msgDTO, currentUser.getId(), currentUser.getRole());

        // Instradamento del messaggio verso il canale corretto
        String topicDestinazione = "/topic/user/" + msgDTO.getChatUserId();

        log.info("Instradamento messaggio del messaggio verso {}", topicDestinazione);
        messagingTemplate.convertAndSend(topicDestinazione, msg);
    }

    /**
     * Metodo che richiama il gestore della presa in carico della chat per gli amministratori della federazione
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @param adminId    Id dell'amministratore che sta cercando di prendere in carico la chat
     */
    @Override
    public void takeCharge(String chatUserId, String adminId) {
        assignmentManager.assignChat(chatUserId, adminId);
    }

    /**
     * Metodo che richiama il gestore del rilascio della chat presa in carico da un amministratore della federazione
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     */
    @Override
    public void releaseChat(String chatUserId) {
        assignmentManager.releaseChat(chatUserId);
    }

}