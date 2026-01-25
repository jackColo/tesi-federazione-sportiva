package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.user.JwtResponseDTO;
import com.tesi.federazione.backend.dto.user.LogUserDTO;
import com.tesi.federazione.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller per la gestione delle operazioni di autenticazione.
 * Espone l'endpoint per il login (generazione del token)
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint di Login.
     * Riceve email e password, verifica le credenziali tramite l'AuthenticationManager di Spring
     * e, se corrette, genera e restituisce un Token JWT.
     *
     * @param loginDTO Oggetto contenente email e password dell'utente che richiede l'autenticazione.
     * @return ResponseEntity<JwtResponseDTO> contenente il JWT e i dati essenziali dell'utente.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@RequestBody LogUserDTO loginDTO) {
        log.info("Richiesta di autenticazione da {}", loginDTO.getEmail());
        JwtResponseDTO jwt = authService.authenticateUser(loginDTO);

        log.info("L'utente {} è stato autenticato!", loginDTO.getEmail());

        // Restituisco il token al client per salvarlo nella localStorage
        return ResponseEntity.ok(jwt);
    }
}