package com.tesi.federazione.backend.factory.user;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import org.springframework.stereotype.Component;

/**
 * Implementazione del Creator per gli utenti con ruolo CLUB_MANAGER
 * Viene registrato nel contesto Spring con il nome "CLUB_MANAGER" (tramite @Component("CLUB_MANAGER)),
 * che corrisponde esattamente alla stringa del ruolo passata nel DTO.
 */
@Component("CLUB_MANAGER")
public class ClubManagerCreator implements UserCreator {

    /**
     * Crea un'istanza di ClubManager.
     * Se presente nel DTO, associa immediatamente il manager al club gestito.
     */
    @Override
    public User createUser(CreateUserDTO dto) {
        ClubManager clubManager = new ClubManager();
        if (dto.getClubId() != null) {
            clubManager.setManagedClub(dto.getClubId());
        }
        return clubManager;
    }
}
