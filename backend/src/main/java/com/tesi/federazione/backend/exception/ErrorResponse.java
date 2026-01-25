package com.tesi.federazione.backend.exception;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Classe che definisce la struttura della risposta degli errori intercettati dall'exceptionHandler
 */
@Data
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
