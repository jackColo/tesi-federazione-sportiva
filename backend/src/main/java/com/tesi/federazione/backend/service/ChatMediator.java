package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.dto.chat.ChatMessageOutputDTO;
import com.tesi.federazione.backend.model.User;

import java.util.List;

/**
 * Interfacce del mediatore tra WebSocket - DB - Gestore dell'assegnazione delle chat per gli amministratori
 */
public interface ChatMediator {

    /**
     * Firma del metoodo per instradare il messaggio nel canale corretto ed effettuare le opportune verifiche
     * sui permessi d'accesso in base al ruolo dell'utente.
     *
     * @param message Messaggio in input
     * @param currentUser Utente che sta inviando il messaggio
     */
    void routeMessage(ChatMessageInputDTO message, User currentUser);


    /**
     * Firma del metodo che richiama il gestore della presa in carico della chat per gli amministratori della federazione
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @param adminId Id dell'amministratore che sta cercando di prendere in carico la chat
     * @return boolean Booleano che indica il permesso concesso o negato
     */
    boolean takeCharge(String chatUserId, String adminId);


    /**
     * Firma del metodo che richiama il gestore del rilascio della chat presa in carico da un amministratore della federazione
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @param adminId Id dell'amministratore che sta richiedendo il rilascio della chat
     */
    void releaseChat(String chatUserId, String adminId);

    /**
     * Firma del metodo per recuperare l'elenco di tutti i messaggi inviati nella chat tra amministratori e
     * clubManager con id 'clubUserId' utilizzato alla riapertura della connessione WebSocket.
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @return List<ChatMessageOutputDTO> Lista ordinata dello storico dei messaggi appartenenti alla chat
     */
    List<ChatMessageOutputDTO> getAllChatMessages(String chatUserId);

}
