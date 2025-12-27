package com.tesi.federazione.backend.state.athlete;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Athlete;

public class AcceptedState implements AthleteState {

    @Override
    public void next(Athlete athlete) {

    }

    @Override
    public void expire(Athlete athlete) {
        athlete.setState(new ExpiredState());
        athlete.setAffiliationStatus(AffiliationStatus.EXPIRED);
    }

    @Override
    public boolean canOperate() {
        return true;
    }
}
