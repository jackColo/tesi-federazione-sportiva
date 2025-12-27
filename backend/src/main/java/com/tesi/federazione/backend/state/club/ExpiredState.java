package com.tesi.federazione.backend.state.club;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Club;

public class ExpiredState implements ClubState {

    @Override
    public void next(Club club) {
        club.setState(new AcceptedState());
        club.setAffiliationStatus(AffiliationStatus.ACCEPTED);
    }

    @Override
    public void expire(Club club) {
        throw new IllegalStateException("Club già disattivato");
    }

    @Override
    public boolean canOperate() {
        return false;
    }
}
