package com.tesi.federazione.backend.state.club;

import com.tesi.federazione.backend.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Club;

public class AcceptedState implements ClubState {

    @Override
    public void next(Club club) {

    }

    @Override
    public void expire(Club club) {
        club.setState(new ExpiredState());
        club.setAffiliationStatus(AffiliationStatus.EXPIRED);
    }

    @Override
    public boolean canOperate() {
        return true;
    }
}
