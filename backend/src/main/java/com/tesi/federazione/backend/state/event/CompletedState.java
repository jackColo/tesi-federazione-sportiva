package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;

public class CompletedState implements EventState {

    @Override
    public void resumeEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile riprogrammato l'evento!");
    }

    @Override
    public void openRegistrations(Event event) {
        throw new ActionNotAllowedException("Impossibile aprire le iscrizioni, l'evento ha già avuto luogo!");
    }

    @Override
    public void closeRegistrations(Event event) {
        throw new ActionNotAllowedException("Impossibile chiudere le iscrizioni, l'evento ha già avuto luogo!");
    }

    @Override
    public void completeEvent(Event event) {
        throw new ActionNotAllowedException("L'evento è già terminato!");
    }

    @Override
    public void cancelEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile cancellare, l'evento ha già avuto luogo!");
    }

    @Override
    public void validateRegistration(Event event) {
        throw new ActionNotAllowedException("Impossibile registrarsi, l'evento ha già avuto luogo!");
    }

}
