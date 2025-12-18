package com.tesi.federazione.backend.factory.user;

import com.tesi.federazione.backend.model.FederationManager;
import com.tesi.federazione.backend.model.User;
import org.springframework.stereotype.Component;

@Component("FEDERATION_MANAGER")
public class FederationManagerCreator implements UserCreator {
    @Override
    public User createUser() {
        return new FederationManager();
    }
}
