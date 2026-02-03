package com.tesi.federazione.backend.security;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
public class ClubApprovalAspect {

    private final UserRepository userRepository;
    private final ClubService clubService;

    // Grazie a @Before, questo codice viene eseguito prima di qualsiasi metodo annotato con @RequiresClubApproval"
    @Before("@annotation(com.tesi.federazione.backend.security.RequiresClubApproval)")
    public void checkClubStatus() {
        // Recupera l'utente autenticato dal contesto di sicurezza
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // Controlla se è un Club Manager
        if (user instanceof ClubManager manager) {
            ClubDTO club = clubService.getClubById(manager.getManagedClub());
            // Se la prima affiliazione del club non è stata approvata, lancia eccezione
            if (!club.getAffiliationStatus().equals(AffiliationStatus.ACCEPTED) && club.getFirstAffiliationDate() == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "L'affiliazione del club deve essere accettata per poter procedere.");
            }
        }
        // Se non è un manager o se è approvato, il codice prosegue.
    }
}