package com.tesi.federazione.backend.factory.state;

import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.state.event.*;

/**
 * Factory responsabile della creazione delle istanze concrete di EventState.
 * Questo componente permette di "reidratare" il comportamento dell'oggetto Evento trasformando
 * l'enumerazione "EventStatus" salvat nel database nella corrispondente classe logica.
 */
public class EventStateFactory {
    /**
     * Restituisce l'istanza dello stato corrispondente allo status fornito.
     *
     * @param status L'enum che rappresenta lo stato attuale dell'evento.
     * @return L'istanza concreta di EventState
     * @throws IllegalArgumentException Se lo status fornito è null o non è mappato su una classe State.
     */
    public static EventState getInitialState(EventStatus status) {
        return switch (status) {
            case SCHEDULED -> new ScheduledState();
            case REGISTRATION_OPEN ->  new RegistrationOpenState();
            case REGISTRATION_CLOSED ->   new RegistrationClosedState();
            case COMPLETED ->   new CompletedState();
            case CANCELLED ->  new CancelledState();
            default -> throw new ActionNotAllowedException("Stato non gestito: " + status);
        };
    }
}
