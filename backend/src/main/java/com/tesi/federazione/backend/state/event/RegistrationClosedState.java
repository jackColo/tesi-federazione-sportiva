package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistrationClosedState implements EventState {

    @Override
    public void openRegistrations(Event event) {
        event.setStatus(EventStatus.REGISTRATION_OPEN);

        event.setState(new RegistrationOpenState());

        // inserire invio notifica a tutti i manager dei club attivi

        log.info("Iscrizioni per l'evento {} riaperte correttamente!", event.getId());
    }

    @Override
    public void closeRegistrations(Event event) {
        throw new IllegalStateException("Le iscrizioni sono già chiuse!");
    }

    @Override
    public void completeEvent(Event event) {
        event.setStatus(EventStatus.COMPLETED);

        event.setState(new CompletedState());

        log.info("Evento {} completato correttamente!", event.getId());
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
        throw new IllegalStateException("Il periodo di registrazione è terminato!");
    }

    @Override
    public EventStatus getStatus() {
        return EventStatus.REGISTRATION_CLOSED;
    }
}
