package com.tesi.federazione.backend.security;

import com.tesi.federazione.backend.model.User;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public String getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public boolean isFederationManager() {
        return hasAuthority("FEDERATION_MANAGER");
    }

    public boolean isClubManager() {
        return hasAuthority("CLUB_MANAGER");
    }

    public boolean isAthlete() {
        return hasAuthority("ATHLETE");
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new AuthorizationDeniedException("User not authorized!");
    }

    private boolean hasAuthority(String authority) {
        User user = getCurrentUser();
        return user != null && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

}
