package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.user.JwtResponseDTO;
import com.tesi.federazione.backend.dto.user.LogUserDTO;

/**
 * Interfaccia per il service che gestisce le operazioni di autenticazione e sicurezza.
 * Gestisce la verifica delle credenziali e la generazione dei token di accesso.
 */
public interface AuthService {

    /**
     * Gestisce il processo di login dell'utente verificando le credenziali e, in caso di successo,
     * genera e restituisce un token JWT valido.
     *
     * @param loginDTO DTO contenente le credenziali dell'utente (email e password).
     * @return JwtResponseDTO Oggetto contenente il token JWT e i dati essenziali dell'utente.
     */
    JwtResponseDTO authenticateUser(LogUserDTO loginDTO);
}