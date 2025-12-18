package com.tesi.federazione.backend.state.club;

import com.tesi.federazione.backend.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Club;

public class SubmittedState implements ClubState {

    @Override
    public void next(Club club) {
        club.setState(new AcceptedState());
        club.setAffiliationStatus(AffiliationStatus.ACCEPTED);
    }

    @Override
    public void expire(Club club) {
        club.setState(new ExpiredState());
        club.setAffiliationStatus(AffiliationStatus.EXPIRED);
    }

    @Override
    public boolean canOperate() {
        return false;
    }
}
