package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import lombok.extern.slf4j.Slf4j;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;

@Slf4j
public class ScheduledState implements EventState {

    @Override
    public void resumeEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile riprogrammato l'evento!");
    }

    @Override
    public void openRegistrations(Event event) {
        event.setStatus(EventStatus.REGISTRATION_OPEN);

        event.setState(new RegistrationOpenState());

        // inserire invio notifica a tutti i manager dei club attivi

        log.info("Apertura iscrizioni per l'evento {} effettuata correttamente!", event.getId());
    }

    @Override
    public void closeRegistrations(Event event)  {
        throw new ActionNotAllowedException("Impossibile chiudere le iscrizioni: non sono ancora state aperte!");
    }

    @Override
    public void completeEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile completare l'evento: non ha ancora ricevuto iscrizioni");
    }

    @Override
    public void cancelEvent(Event event) {
        event.setStatus(EventStatus.CANCELLED);

        event.setState(new CancelledState());

        // inserire invio notifica a tutti gli atleti iscritti e ai manager dei club iscritti

        log.info("Evento {} cancellato correttamente!", event.getId());
    }

    @Override
    public void validateRegistration(Event event) {
        throw new ActionNotAllowedException("Le registrazioni non sono ancora state aperte.");
    }
}
