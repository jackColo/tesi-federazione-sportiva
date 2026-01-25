package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import lombok.extern.slf4j.Slf4j;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;

/**
 * Implementazione dello stato SCHEDULED (Programmato).
 * Rappresenta lo stato iniziale di un evento appena creato.
 * In questa fase l'evento è visibile ma le iscrizioni possono essere
 * salvate solo in bozza e le uniche transizioni di stato possibili sono verso
 * gli stati REGISTRATION_OPEN o CANCELLED.
 */
@Slf4j
public class ScheduledState implements EventState {

    /**
     * Tenta di ripristinare l'evento allo stato programmato.
     * Dato che siamo già in questo stato, l'azione non ha senso.
     */
    @Override
    public void resumeEvent(Event event) {
        log.error("L'evento {} è già nello stato SCHEDULED", event.getId());
        throw new ActionNotAllowedException("Impossibile riprogrammare l'evento!");
    }

    /**
     * Apre le iscrizioni (Transizione a REGISTRATION_OPEN).
     * Permette ai club manager di inviare le iscrizioni definitive.
     */
    @Override
    public void openRegistrations(Event event) {
        event.setStatus(EventStatus.REGISTRATION_OPEN);
        event.setState(new RegistrationOpenState());
        log.info("Apertura iscrizioni per l'evento {} effettuata correttamente!", event.getId());
    }

    /**
     * Chiude le iscrizioni: l'azione non ha senso perchè le registrazioni
     * non sono mai state aperte.
     */
    @Override
    public void closeRegistrations(Event event)  {
        log.error("L'evento {} è nello stato SCHEDULED, le iscrizioni non possono essere aperte", event.getId());
        throw new ActionNotAllowedException("Impossibile chiudere le iscrizioni: non sono ancora state aperte!");
    }

    /**
     * Completa l'evento: l'azione non ha senso perchè l'evento non si è ancora svolto
     */
    @Override
    public void completeEvent(Event event) {
        log.error("L'evento {} è nello stato SCHEDULED, non è possibile completare eventi senza iscrizioni", event.getId());
        throw new ActionNotAllowedException("Impossibile completare l'evento: non ha ancora ricevuto iscrizioni");
    }

    /**
     * Annulla l'evento (Transizione a CANCELLED).
     */
    @Override
    public void cancelEvent(Event event) {
        event.setStatus(EventStatus.CANCELLED);
        event.setState(new CancelledState());
        log.info("Evento {} cancellato correttamente!", event.getId());
    }

    /**
     * Valida la possibilità creare o modificare un'iscrizione.
     * In fase SCHEDULED, sono permesse le iscrizioni come bozze (DRAFT) per permettere ai club manager di preparare i dati.
     */
    @Override
    public void validateRegistration(Event event, boolean isDraft, boolean isFederationManager) {
        if (!isDraft) {
            log.error("Sono ammesse solo registrazioni in bozza");
            throw new ActionNotAllowedException("Le registrazioni non sono ancora state aperte: puoi creare solo le bozze");
        }
    }
}
