package com.tesi.federazione.backend.security;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.exception.UnauthorizedException;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Classe Aspect che implementa la logica di controllo definita dall'annotazione
 * @RequiresClubApproval: intercetta le chiamate ai metodi protetti e verifica che, se l'utente
 * è un Club Manager, il suo club abbia completato con successo la prima fase di affiliazione,
 * impedendo l'esecuzione del metodo in caso contrario.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ClubApprovalAspect {

    private final UserRepository userRepository;
    private final ClubService clubService;

    /**
     * Metodo eseguito automaticamente prima di qualsiasi funzionalità marcata con @RequiresClubApproval
     * (grazie all'utilizzo dell'annotazione @Before).
     * Recupera l'utente corrente e, nel caso specifico dei Club Manager, verifica che il club gestito
     * non sia ancora in attesa della prima approvazione (stato pendente e data di prima affiliazione assente),
     * bloccando l'operazione se il requisito non è soddisfatto
     */
    @Before("@annotation(com.tesi.federazione.backend.security.RequiresClubApproval)")
    public void checkClubStatus() {
        // Recupera l'utente autenticato dal contesto di sicurezza
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Non esiste un utente che corrisponde all'autenticazione indicata!"));

        // Controlla se è un Club Manager
        if (user instanceof ClubManager manager) {
            ClubDTO club = clubService.getClubById(manager.getManagedClub());
            // Se la prima affiliazione del club non è stata approvata, lancia eccezione
            if (!club.getAffiliationStatus().equals(AffiliationStatus.ACCEPTED) && club.getFirstAffiliationDate() == null) {
                throw new UnauthorizedException("L'affiliazione del club deve essere accettata per poter procedere.");
            }
        }
        // Se non è un manager o se è approvato, il codice prosegue.
    }
}