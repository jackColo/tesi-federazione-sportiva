package com.tesi.federazione.backend.exception;


/**
 * Eccezione personalizzata per la gestione delle violazioni dei permessi di accesso alle risorse o di cambi di stato non consentiti.
 * Questa eccezione viene intercettata dal ControllerExceptionHandler e mappata
 * su una risposta HTTP 400 (BAD REQUEST).
 */
public class ActionNotAllowedException extends RuntimeException {

    /**
     * Costruisce l'eccezione con un messaggio descrittivo dell'errore.
     * @param message Il messaggio che spiega perché l'azione è stata negata.
     */
    public ActionNotAllowedException(String message) {
        super(message);
    }
}
