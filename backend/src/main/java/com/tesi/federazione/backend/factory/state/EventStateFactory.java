package com.tesi.federazione.backend.factory.state;

import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.state.event.*;

public class EventStateFactory {
    public static EventState getInitialState(EventStatus status) {
        return switch (status) {
            case SCHEDULED -> new ScheduledState();
            case REGISTRATION_OPEN ->  new RegistrationOpenState();
            case REGISTRATION_CLOSED ->   new RegistrationClosedState();
            case COMPLETED ->   new CompletedState();
            case CANCELLED ->  new CancelledState();
        };
    }
}
