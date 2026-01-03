package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class CancelledState implements EventState {

    @Override
    public void resumeEvent(Event event) {
        if (event.getDate().isBefore(LocalDate.now())) {
            throw new ActionNotAllowedException("Non puoi riattivare un evento scaduto");
        }
        event.setStatus(EventStatus.SCHEDULED);

        event.setState(new ScheduledState());

        // inserire invio notifica a tutti i manager dei club che erano iscritti quando è stato cancellato l'evento

        log.info("Evento {} riprogrammato correttamente!", event.getId());
    }

    @Override
    public void openRegistrations(Event event) {
        throw new ActionNotAllowedException("Impossibile aprire le iscrizioni, l'evento è stato cancellato!");
    }

    @Override
    public void closeRegistrations(Event event) {
        throw new ActionNotAllowedException("Impossibile chiudere le iscrizioni, l'evento è stato cancellato!");
    }

    @Override
    public void completeEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile completare, l'evento è stato cancellato!");
    }

    @Override
    public void cancelEvent(Event event) {
        throw new ActionNotAllowedException("L'evento è già stato cancellato!");
    }

    @Override
    public void validateRegistration(Event event) {
        throw new ActionNotAllowedException("Impossibile registrarsi, l'evento è stato cancellato!.");
    }

}
