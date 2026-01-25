package com.tesi.federazione.backend.exception;

/**
 * Eccezione personalizzata per la gestione dei tentativi di accesso a risorse protette senza i privilegi necessari.
 * Questa eccezione viene intercettata dal ControllerExceptionHandler e mappata
 * su una risposta HTTP 403 (FORBIDDEN).
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Costruisce l'eccezione con un messaggio descrittivo dell'errore.
     * @param message Il messaggio che spiega perché l'azione è stata negata.
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
