package com.tesi.federazione.backend.factory.user;

import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import org.springframework.stereotype.Component;

@Component("CLUB_MANAGER")
public class ClubManagerCreator implements UserCreator {
    @Override
    public User createUser() {
        return new ClubManager();
    }
}
