package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementazione personalizzata del servizio UserDetailsService di Spring Security per collegare
 * il modulo di spring al db dell'applicazione.
 * Implementa il metodo per recuperare i dettagli dell'utente
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Recupera l'utente dal database tramite la sua email.
     *
     * @param email Email fornita nel form di login.
     * @return L'oggetto User (che estende UserDetails) se trovato.
     * @throws UsernameNotFoundException Se nessun utente corrisponde all'email fornita.
     */
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
    }
}