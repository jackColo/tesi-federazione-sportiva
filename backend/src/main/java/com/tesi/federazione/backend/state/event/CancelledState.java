package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * Implementazione dello stato CANCELLED (Cancellato).
 * L'evento è stato annullato.
 * L'unica transizione possibile è quella per ripristinarlo allo stato SCHEDULED
 * ma solo se la data dell'evento non è ancora trascorsa.
 */
@Slf4j
public class CancelledState implements EventState {

    /**
     * Tenta di ripristinare l'evento allo stato programmato (Transizione verso SCHEDULED).
     * Permesso SOLO se la data dell'evento è nel futuro.
     */
    @Override
    public void resumeEvent(Event event) {
        if (event.getDate().isBefore(LocalDate.now())) {
            throw new ActionNotAllowedException("Non puoi riattivare un evento scaduto");
        }
        event.setStatus(EventStatus.SCHEDULED);
        event.setState(new ScheduledState());

        log.info("Evento {} riprogrammato correttamente!", event.getId());
    }

    /**
     * Apre le iscrizioni.
     * Non permesso su un evento cancellato.
     */
    @Override
    public void openRegistrations(Event event) {
        throw new ActionNotAllowedException("Impossibile aprire le iscrizioni, l'evento è stato cancellato!");
    }

    /**
     * Chiude le iscrizioni.
     * Non permesso su un evento cancellato.
     */
    @Override
    public void closeRegistrations(Event event) {
        throw new ActionNotAllowedException("Impossibile chiudere le iscrizioni, l'evento è stato cancellato!");
    }

    /**
     * Completa l'evento.
     * Non permesso su un evento cancellato.
     */
    @Override
    public void completeEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile completare, l'evento è stato cancellato!");
    }

    /**
     * Annulla l'evento.
     * Non permesso su un evento già cancellato.
     */
    @Override
    public void cancelEvent(Event event) {
        throw new ActionNotAllowedException("L'evento è già stato cancellato!");
    }

    /**
     * Valida la possibilità di iscrizione.
     * Negata perchè l'evento è cancellato.
     */
    @Override
    public void validateRegistration(Event event, boolean isDraft, boolean isFederationManager) {
        throw new ActionNotAllowedException("Impossibile registrarsi, l'evento è stato cancellato!.");
    }

}
