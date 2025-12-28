package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;

public interface EventState {
    void openRegistrations(Event event);
    void closeRegistrations(Event event);
    void completeEvent(Event event);
    void cancelEvent(Event event);

    void validateRegistration(Event event);

    EventStatus getStatus();
}