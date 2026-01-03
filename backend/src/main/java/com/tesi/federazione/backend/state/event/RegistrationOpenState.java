package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import lombok.extern.slf4j.Slf4j;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;

@Slf4j
public class RegistrationOpenState implements EventState {

    @Override
    public void resumeEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile riprogrammato l'evento!");
    }

    @Override
    public void openRegistrations(Event event) {
        throw new ActionNotAllowedException("Le iscrizioni sono già aperte!");
    }

    @Override
    public void closeRegistrations(Event event) {
        event.setStatus(EventStatus.REGISTRATION_CLOSED);

        event.setState(new RegistrationClosedState());

        // inserire invio notifica a tutti i manager dei club attivi

        log.info("Chiusura iscrizioni per l'evento {} effettuata correttamente!", event.getId());
    }

    @Override
    public void completeEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile completare l'evento, le iscrizioni sono ancora state aperte!");
    }

    @Override
    public void cancelEvent(Event event) {
        throw new ActionNotAllowedException("Per cancellare l'evento le iscrizioni devono essere chiuse.");
    }

    @Override
    public void validateRegistration(Event event) {

    }
}
