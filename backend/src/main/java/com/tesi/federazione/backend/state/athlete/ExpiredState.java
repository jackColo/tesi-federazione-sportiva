package com.tesi.federazione.backend.state.athlete;

import com.tesi.federazione.backend.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.Club;

public class ExpiredState implements AthleteState {

    @Override
    public void next(Athlete athlete) {
        athlete.setState(new AcceptedState());
        athlete.setAffiliationStatus(AffiliationStatus.ACCEPTED);
    }

    @Override
    public void expire(Athlete athlete) {
    }

    @Override
    public boolean canOperate() {
        return false;
    }
}
