package com.tesi.federazione.backend.factory.state;

import com.tesi.federazione.backend.enums.AffiliationStatus;
import com.tesi.federazione.backend.state.athlete.AthleteState;
import com.tesi.federazione.backend.state.athlete.AcceptedState;
import com.tesi.federazione.backend.state.athlete.ExpiredState;
import com.tesi.federazione.backend.state.athlete.SubmittedState;

public class AthleteStateFactory {
    public static AthleteState getInitialState(AffiliationStatus status) {
        return switch (status) {
            case SUBMITTED -> new SubmittedState();
            case ACCEPTED -> new AcceptedState();
            case EXPIRED -> new ExpiredState();
            default -> throw new IllegalArgumentException("Stato non valido");
        };
    }
}
