package com.tesi.federazione.backend.security;

import com.tesi.federazione.backend.exception.UnauthorizedException;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Metodi di Utility per effettuare controlli relativi all'utente che invia le richieste tramite il client.
 * - verifiche sul ruolo
 * - verifiche sui permessi di accesso ai club
 * - get di Id, Email, Club
 */
@Component
public class SecurityUtils {

    /**
     * Utility per recuperare l'ID dell'utente autenticato
     * @return ID dell'untete autenticato
     */
    public String getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * Utility per recuperare l'email dell'utente autenticato
     * @return Email dell'untete autenticato
     */
    public String getCurrentUserEmail() {
        User user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    /**
     * Utility per verificare che l'utente autenticato sia un FEDERATION_MANAGER
     * @return true se l'utente è un ATHLFEDERATION_MANAGERETE, false altrimenti
     */
    public boolean isFederationManager() {
        return hasAuthority("FEDERATION_MANAGER");
    }

    /**
     * Utility per verificare che l'utente autenticato sia un CLUB_MANAGER
     * @return true se l'utente è un CLUB_MANAGER, false altrimenti
     */
    public boolean isClubManager() {
        return hasAuthority("CLUB_MANAGER");
    }

    /**
     * Utility per verificare che l'utente autenticato sia un ATHLETE
     * @return true se l'utente è un ATHLETE, false altrimenti
     */
    public boolean isAthlete() {
        return hasAuthority("ATHLETE");
    }

    /**
     * Utility per verificare l'appartenenza a un determinato club dell'utente che sta facendo richiesta
     * @param clubId Id del club da verificare
     * @return true se appartiene, false altrimenti
     */
    public boolean isMyClub(String clubId) {
        User user = getCurrentUser();
        if (user instanceof ClubManager) {
            ClubManager clubManager = (ClubManager) user;
            return clubManager.getManagedClub().equals(clubId);
        } else if (user instanceof Athlete) {
            Athlete athlete = (Athlete) user;
            return athlete.getId().equals(clubId);
        }
        return false;
    }

    /**
     * Utility per recuperare l'id del club a cui appartiene l'utente che sta facendo richiesta
     * @return String dell'id del club trovato
     */
    public String getUserClub() {
        User user = getCurrentUser();
        if (user instanceof ClubManager) {
            ClubManager clubManager = (ClubManager) user;
            return clubManager.getManagedClub();
        } else if (user instanceof Athlete) {
            Athlete athlete = (Athlete) user;
            return athlete.getId();
        }
        return null;
    }

    /**
     * Utility privata per recuperare l'istanza dell'utente autenticato
     * @return User dell'utente autenticato
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new UnauthorizedException("Utente non autenticato!");
    }

    /**
     *  Utility privata per verificare il ruolo dell'utente autenticato
     * @param authority Ruolo da verificare
     * @return true se il ruolo coincide con quello richiesto, false altrimenti
     */
    private boolean hasAuthority(String authority) {
        User user = getCurrentUser();
        return user != null && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

}
