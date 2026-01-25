package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;

/**
 * Implementazione dello stato COMPLETED (Completato).
 * Stato terminale: l'evento si è svolto. Nessuna operazione di modifica stato o iscrizione è più permessa.
 */
public class CompletedState implements EventState {

    /**
     * Tenta di ripristinare l'evento.
     * Non permesso su un evento già concluso.
     */
    @Override
    public void resumeEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile riprogrammare l'evento!");
    }

    /**
     * Apre le iscrizioni.
     * Non permesso su un evento già concluso.
     */
    @Override
    public void openRegistrations(Event event) {
        throw new ActionNotAllowedException("Impossibile aprire le iscrizioni, l'evento ha già avuto luogo!");
    }

    /**
     * Chiude le iscrizioni.
     * Non permesso su un evento già concluso.
     */
    @Override
    public void closeRegistrations(Event event) {
        throw new ActionNotAllowedException("Impossibile chiudere le iscrizioni, l'evento ha già avuto luogo!");
    }

    /**
     * Completa l'evento.
     * Non permesso su un evento già concluso.
     */
    @Override
    public void completeEvent(Event event) {
        throw new ActionNotAllowedException("L'evento è già terminato!");
    }

    /**
     * Annulla l'evento.
     * Non permesso su un evento già concluso.
     */
    @Override
    public void cancelEvent(Event event) {
        throw new ActionNotAllowedException("Impossibile cancellare, l'evento ha già avuto luogo!");
    }

    /**
     * Valida la possibilità di iscrizione.
     * Negata perchè l'evento è passato.
     */
    @Override
    public void validateRegistration(Event event, boolean isDraft, boolean isFederationManager) {
        throw new ActionNotAllowedException("Impossibile registrarsi, l'evento ha già avuto luogo!");
    }

}
