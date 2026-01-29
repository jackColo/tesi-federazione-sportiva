package com.tesi.federazione.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesi.federazione.backend.dto.user.JwtResponseDTO;
import com.tesi.federazione.backend.dto.user.LogUserDTO;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.security.JwtUtils;
import com.tesi.federazione.backend.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test per Login: Successo (200 OK) e restituisce Token")
    void success() throws Exception {
        LogUserDTO loginRequest = new LogUserDTO();
        loginRequest.setEmail("test@email.com");
        loginRequest.setPassword("password123");

        JwtResponseDTO jwtResponseDTO = new JwtResponseDTO(
                "token",
                "1234",
                "test@email.com",
                Role.ATHLETE
        );

        when(authService.authenticateUser(any(LogUserDTO.class))).thenReturn(jwtResponseDTO);

        // Eseguo chiamata POST simulata
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }
}