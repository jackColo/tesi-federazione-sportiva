package com.tesi.federazione.backend.factory.user;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.model.FederationManager;
import com.tesi.federazione.backend.model.User;
import org.springframework.stereotype.Component;

/**
 * Implementazione del Creator per gli utenti con ruolo FEDERATION_MANAGER
 * Viene registrato nel contesto Spring con il nome "FEDERATION_MANAGER" (tramite @Component("FEDERATION_MANAGER)),
 * che corrisponde esattamente alla stringa del ruolo passata nel DTO.
 */
@Component("FEDERATION_MANAGER")
public class FederationManagerCreator implements UserCreator {

    /**
     * Crea un'istanza di FederationManager.
     * Attualmente questa figura non richiede campi specifici aggiuntivi in fase di creazione.
     */
    @Override
    public User createUser(CreateUserDTO dto) {
        return new FederationManager();
    }

    /**
     * Aggiorno i dati dell'istanza FederationManager passata in oggetto, sovrascrivendone i campi passati tramite dto
     * Attualmente vuota perchè non sono previsti campi aggiuntivi
     */
    @Override
    public void updateUser(User user, CreateUserDTO dto) {
    }
}
