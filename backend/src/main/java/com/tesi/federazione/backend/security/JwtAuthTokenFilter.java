package com.tesi.federazione.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro di sicurezza personalizzato che viene eseguito a ogni richiesta HTTP.
 * Intercetta le richieste e verifica la presenza di un token JWT nell'header "Authorization".
 * Se il token è valido, autentica l'utente nel contesto di sicurezza di Spring.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    /**
     * Metodo principale che contiene la logica del filtro:
     * 1. Controlla l'header Authorization.
     * 2. Estrae il token e l'username (email).
     * 3. Se l'utente non è già autenticato, valida il token.
     * 4. Se valido, crea l'oggetto Authentication e lo imposta nel SecurityContextHolder.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Se l'header manca o non inizia con "Bearer" la richiesta prosegue nella catena dei filtri
        // senza autenticazione per verificare se sia una richiesta pubblica
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Rimozione del prefisso "Bearer "
        jwt = authHeader.substring(7);

        // Estrazione dello username dal token
        userEmail = jwtUtils.extractUsername(jwt);

        // Se lo username esiste e non esiste ancora un autenticazione per l'utente nel contesto attuale
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Caricamento dei dettagli dell'utente
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // validazione dell'utente rispetto ai dettagli dell'utente
            if (jwtUtils.isTokenValid(jwt, userDetails)) {
                // Se l'utente è valido creo l'oggetto di autenticazione per Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // aggiunta dei dettagli della richiesta all'oggetto appena creato
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Impostazione dell'oggetto di autenticazione nel contesto
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continuo la filterChain passando la richiesta al prossimo controllo (configurata in WebSecurityConfig)
        filterChain.doFilter(request, response);
    }
}