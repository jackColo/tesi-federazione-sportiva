package com.tesi.federazione.backend.factory.user;

import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.User;
import org.springframework.stereotype.Component;

@Component("ATHLETE")
public class AthleteCreator implements UserCreator {
    @Override
    public User createUser() {
        return new Athlete();
    }
}
