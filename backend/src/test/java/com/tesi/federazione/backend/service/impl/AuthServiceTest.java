package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.JwtResponseDTO;
import com.tesi.federazione.backend.dto.user.LogUserDTO;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.security.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario per Login e generazione di token JWT
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    /**
     * Test per verificare che con le credenziali corrette venga restituito
     * il token correttamente formato
     */
    @Test
    @DisplayName("Test successo login: utente verificato e token generato")
    public void authenticateUser_success() {
        // Simulo i dati di autenticazione inviati dal client
        LogUserDTO logUserDTO = new LogUserDTO();
        logUserDTO.setEmail("email@test.com");
        logUserDTO.setPassword("password");

        // Simuliamo l'autenticazione riuscita
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // Simuliamo generazione del token
        JwtResponseDTO expectedTokenDTO = new JwtResponseDTO(
                "eyJhbGciOiJIUzI1Ni...",
                "user-123",
                "mario@test.com",
                Role.ATHLETE
        );
        when(jwtUtils.generateToken(any())).thenReturn(expectedTokenDTO);

        // Chiamo il metodo da testare
        JwtResponseDTO generatedToken = authService.authenticateUser(logUserDTO);

        // Asserzioni per verificare il metodo
        assertNotNull(generatedToken);
        assertEquals(expectedTokenDTO, generatedToken);

        // Verifico che authenticationManager venga effettivamente chiamato per controllare le credenziali
        verify(authenticationManager, times(1)).authenticate(any());
    }

    /**
     * Test per verificare che, con le credenziali errate, l'errore venga
     * lanciato e non venga mai generato il token
     */
    @Test
    @DisplayName("Test fallimento login: Eccezione lanciata e nessun token generato")
    public void authenticateUser_fail() {
        // Simulo i dati di autenticazione inviati dal client
        LogUserDTO logUserDTO = new LogUserDTO();
        logUserDTO.setEmail("email@test.com");
        logUserDTO.setPassword("password");

        // Simuliamo l'autenticazione fallita
        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Verifico che l'eccezione venga effettivamente lanciata e che il token non venga mai generato
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticateUser(logUserDTO);
        });
        verify(jwtUtils, never()).generateToken(any());
    }
}
