package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import lombok.extern.slf4j.Slf4j;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;

/**
 * Implementazione dello stato REGISTRATION_OPEM (registrazioni aperte).
 * In questa fase l'evento è visibile e le iscrizioni possono essere inviate dai club manager anche
 * con stato SUBMITTED, sono in oltre permesse tutte le modifiche alle iscrizioni (sia di contenuto che di stato)
 * L'unica transizione possibili è verso lo stato REGISTRATION_CLOSED.
 */
@Slf4j
public class RegistrationOpenState implements EventState {

    /**
     * Tenta di ripristinare l'evento allo stato programmato.
     * Ripristinare un evento che non è stato cancellato non ha senso.
     */
    @Override
    public void resumeEvent(Event event) {
        log.error("Impossibile ripristinare evento durante la fase d'iscrizione");
        throw new ActionNotAllowedException("Impossibile riprogrammare l'evento!");
    }

    /**
     * Apre le iscrizioni per l'evento.
     * Dato che siamo già in questo stato, l'azione non ha senso.
     */
    @Override
    public void openRegistrations(Event event) {
        log.error("L'evento {} è già nello stato OPEN_REGISTRATION", event.getId());
        throw new ActionNotAllowedException("Le iscrizioni sono già aperte!");
    }


    /**
     * Chiude le iscrizioni (Transizione a REGISTRATION_CLOSED).
     * Termina il periodo di iscrizione all'evento: nè atleti nè club manager potranno più inviare iscrizioni.
     */
    @Override
    public void closeRegistrations(Event event) {
        event.setStatus(EventStatus.REGISTRATION_CLOSED);
        event.setState(new RegistrationClosedState());
        log.info("Chiusura iscrizioni per l'evento {} effettuata correttamente!", event.getId());
    }

    /**
     * Completa l'evento: l'azione non ha senso perchè l'evento non si è ancora svolto
     */
    @Override
    public void completeEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile completare l'evento, le iscrizioni sono ancora state aperte!");
    }


    /**
     * Annulla l'evento: l'azione non è possibile durante la fase di iscrizione.
     */
    @Override
    public void cancelEvent(Event event) {
        throw new ActionNotAllowedException("Per cancellare l'evento le iscrizioni devono essere chiuse.");
    }

    /**
     * Valida la possibilità creare o modificare un'iscrizione.
     */
    @Override
    public void validateRegistration(Event event, boolean isDraft, boolean isFederationManager) {
        log.info("Le iscrizioni per l'evento {} sono aperte, è possibile registrarsi o modificare l'iscrizione.", event.getId());
    }
}
