package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.JwtResponseDTO;
import com.tesi.federazione.backend.dto.user.LogUserDTO;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.security.JwtUtils;
import com.tesi.federazione.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Implementazione del servizio di autenticazione.
 * Contiene la logica per validare le credenziali tramite Spring Security
 * e generare il token di accesso tramite le utility JWT.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Gestisce il processo di login dell'utente.
     *
     * @param loginDTO DTO contenente le credenziali dell'utente (email e password).
     * @return JwtResponseDTO Oggetto contenente il token JWT e i dati essenziali dell'utente.
     *
     * @throws BadCredentialsException Se le credenziali fornite non sono valide.
     */
    @Override
    public JwtResponseDTO authenticateUser(LogUserDTO loginDTO) {

        // Tentativo di autenticazione dell'utente, se fallisce il metodo lancia un eccezione catturata dal ControllerExceptionHandler
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        // Se l'autenticazione va a buon fine estraggo i dati dell'utente
        User userDetails = (User) authentication.getPrincipal();

        // Genero il token jwt costruendo la risposta DTO con i dati dell'utente
        return jwtUtils.generateToken(userDetails);
    }
}