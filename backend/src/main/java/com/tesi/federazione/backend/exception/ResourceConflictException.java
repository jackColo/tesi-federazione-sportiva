package com.tesi.federazione.backend.exception;

/**
 * Eccezione personalizzata per la gestione dei conflitti tra i dati come email duplicate o iscrizione già esistente
 * Questa eccezione viene intercettata dal ControllerExceptionHandler e mappata
 * su una risposta HTTP 409 (CONFLICT).
 */
public class ResourceConflictException extends RuntimeException {

    /**
     * Costruisce l'eccezione con un messaggio descrittivo dell'errore.
     * @param message Il messaggio che spiega il conflitto che ha bloccato la richiesta.
     */
    public ResourceConflictException(String message) {
        super(message);
    }
}
