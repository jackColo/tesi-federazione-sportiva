package com.tesi.federazione.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthTokenFilterTest {

    @Mock private JwtUtils jwtUtils;
    @Mock private UserDetailsService userDetailsService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtAuthTokenFilter jwtFilter;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("No Header: Passa oltre senza autenticare")
    void doFilterInternalTest_NoHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Header Errato: Passa oltre")
    void doFilterInternalTest_BadHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic 12345");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Token Valido: Autentica l'utente")
    void doFilterInternalTest_ValidToken() throws Exception {
        String token = "valid.jwt.token";
        String email = "user@test.com";
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.extractUsername(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtils.isTokenValid(token, userDetails)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    @DisplayName("Token Invalido: Non autentica")
    void doFilterInternalTest_InvalidToken() throws Exception {
        String token = "invalid.jwt.token";
        String email = "user@test.com";
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.extractUsername(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtils.isTokenValid(token, userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}