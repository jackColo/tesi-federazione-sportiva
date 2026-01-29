package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementazione dello stato REGISTRATION_CLOSED (iscrizioni chiuse).
 * In questa fase le iscrizioni sono terminate ma l'evento non si è ancora svolto.
 * Le transizioni possibili sono verso gli stati OPEN_REGISTRATION, COMPLETED, CANCELLED.
 * Sono permesse modifiche alle registrazioni solo da parte degli amministratori
 * (necessario per l'accettazione / rifiuto delle iscrizioni)
 */
@Slf4j
public class RegistrationClosedState implements EventState {

    /**
     * Tenta di ripristinare l'evento allo stato programmato.
     * Operazione non consentita in questa fase.
     */
    @Override
    public void resumeEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile riprogrammare l'evento!");
    }

    /**
     * Riapre le iscrizioni per l'evento (Transizione verso REGISTRATION_OPEN).
     * Permette nuovamente ai manager di iscrivere atleti.
     */
    @Override
    public void openRegistrations(Event event) {
        event.setStatus(EventStatus.REGISTRATION_OPEN);
        event.setState(new RegistrationOpenState());

        log.info("Iscrizioni per l'evento {} riaperte correttamente!", event.getId());
    }

    /**
     * Chiude le iscrizioni.
     * Dato che sono già chiuse la transizione non ha senso
     */
    @Override
    public void closeRegistrations(Event event) {
        throw new ActionNotAllowedException("Le iscrizioni sono già chiuse!");
    }

    /**
     * Completa l'evento (Transizione verso COMPLETED).
     * Da invocare una volta che la manifestazione si è conclusa.
     */
    @Override
    public void completeEvent(Event event) {
        event.setStatus(EventStatus.COMPLETED);
        event.setState(new CompletedState());

        log.info("Evento {} completato correttamente!", event.getId());
    }

    /**
     * Annulla l'evento (Transizione verso CANCELLED).
     * L'evento viene marcato come non svolto.
     */
    @Override
    public void cancelEvent(Event event) {
        event.setStatus(EventStatus.CANCELLED);
        event.setState(new CancelledState());

        log.info("Evento {} cancellato correttamente!", event.getId());
    }

    /**
     * Valida la possibilità di iscrizione.
     * Concessa solo agli amministratori della federazione per permettere di accettare o rifiutare le iscrizioni
     */
    @Override
    public void validateRegistration(Event event, boolean isDraft, boolean isFederationManager) {
        if (!isFederationManager) {
            throw new ActionNotAllowedException("Il periodo di registrazione è terminato!");
        } else {
            log.info("L'amministratore può procedere alla gestione delle iscrizioni");
        }
    }
}
