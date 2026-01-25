package com.tesi.federazione.backend.model.enums;

/**
 * Definisce i ruoli presenti nel sistema e che determinano sia i permessi
 * di accesso alle API sia i permessi di accesso alle risorse.
 */
public enum Role {

    /**
     * Atleta: è l'utente autenticato di base, può gestire i suoi dati e presentare una bozza di iscrizione agli eventi.
     */
    ATHLETE,

    /**
     * Gestore di Club: oltre ai sui dati può gestire i dati del club, i dati dei suoi atleti e le iscrizioni dei propri atleti.
     */
    CLUB_MANAGER,

    /**
     * Amministratore della Federazione: accesso completo al sistema (approvazioni, gestione eventi, chat).
     */
    FEDERATION_MANAGER,
}