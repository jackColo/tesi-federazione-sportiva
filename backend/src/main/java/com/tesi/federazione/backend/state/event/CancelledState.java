package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;

public class CancelledState implements EventState {

    @Override
    public void openRegistrations(Event event) {
        throw new IllegalStateException("Impossibile aprire le iscrizioni, l'evento è stato cancellato!");
    }

    @Override
    public void closeRegistrations(Event event) {
        throw new IllegalStateException("Impossibile chiudere le iscrizioni, l'evento è stato cancellato!");
    }

    @Override
    public void completeEvent(Event event) {
        throw new IllegalStateException("Impossibile completare, l'evento è stato cancellato!");
    }

    @Override
    public void cancelEvent(Event event) {
        throw new IllegalStateException("L'evento è già stato cancellato!");
    }

    @Override
    public void validateRegistration(Event event) {
        throw new IllegalStateException("Impossibile registrarsi, l'evento è stato cancellato!.");
    }

    @Override
    public EventStatus getStatus() {
        return EventStatus.CANCELLED;
    }
}
