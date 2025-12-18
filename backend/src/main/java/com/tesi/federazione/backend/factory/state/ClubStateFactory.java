package com.tesi.federazione.backend.factory.state;

import com.tesi.federazione.backend.enums.AffiliationStatus;
import com.tesi.federazione.backend.state.club.AcceptedState;
import com.tesi.federazione.backend.state.club.ClubState;
import com.tesi.federazione.backend.state.club.ExpiredState;
import com.tesi.federazione.backend.state.club.SubmittedState;

public class ClubStateFactory {
    public static ClubState getInitialState(AffiliationStatus status) {
        return switch (status) {
            case SUBMITTED -> new SubmittedState();
            case ACCEPTED -> new AcceptedState();
            case EXPIRED -> new ExpiredState();
            default -> throw new IllegalArgumentException("Stato non valido");
        };
    }
}
