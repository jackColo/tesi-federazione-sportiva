package com.tesi.federazione.backend.service.chat;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.model.User;

/**
 * Interfacce del mediatore tra WebSocket - Gestore dell'assegnazione delle chat per gli amministratori
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
     */
    void takeCharge(String chatUserId, String adminId);


    /**
     * Firma del metodo che richiama il gestore del rilascio della chat presa in carico da un amministratore della federazione
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @param adminId Id dell'amministratore che sta richiedendo il rilascio della chat
     */
    void releaseChat(String chatUserId, String adminId);

}
