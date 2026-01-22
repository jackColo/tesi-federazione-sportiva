package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.user.JwtResponseDTO;
import com.tesi.federazione.backend.dto.user.LogUserDTO;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

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

        // Tentativo di autenticazione dell'utente, se fallisce il metodo lancia un eccezione catturata dal ControllerExceptionHandler
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        // Se l'autenticazione va a buon fine estraggo i dati dell'utente
        User userDetails = (User) authentication.getPrincipal();

        // Genero il token jwt costruendo la risposta DTO con i dati dell'utente
        JwtResponseDTO jwt = jwtUtils.generateToken(userDetails);

        log.info("L'utente {} è stato autenticato!", loginDTO.getEmail());

        // Restituisco il token al client per salvarlo nella localStorage
        return ResponseEntity.ok(jwt);
    }
}