package com.tesi.federazione.backend.exception;
/**
 * Eccezione personalizzata per la gestione dei casi in cui una risorsa richiesta non viene trovata.
 * Questa eccezione viene intercettata dal ControllerExceptionHandler e mappata
 * su una risposta HTTP 404 (NOT FOUND).
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Costruisce l'eccezione con un messaggio descrittivo dell'errore.
     * @param message Il messaggio che spiega perché l'azione è stata negata.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
