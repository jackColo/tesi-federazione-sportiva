package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;

/**
 * Interfaccia per il design patter State che definisce i metodi che ogni stato concreto dell'evento deve implementare.
 * Ogni metodo rappresenta un'azione che può comportare una transizione di stato o una validazione logica.
 * Se l'azione non è permessa viene lanciata una ActionNotAllowedException
 */
public interface EventState {

    /**
     * Riporta l'evento allo stato SCHEDULED (pianificato).
     * Utile per riattivare eventi annullati o sospesi.
     *
     * @param event L'evento su cui operare.
     * @throws ActionNotAllowedException Se lo stato attuale non permette il ripristino (es. evento già concluso).
     */
    void resumeEvent(Event event);

    /**
     * Apre le iscrizioni per l'evento (transizione a REGISTRATION_OPEN).
     *
     * @param event L'evento su cui operare.
     * @throws ActionNotAllowedException Se l'evento non è pronto o le iscrizioni sono già chiuse/concluse.
     */
    void openRegistrations(Event event);

    /**
     * Chiude le iscrizioni per l'evento (transizione a REGISTRATION_CLOSED).
     * Impedisce nuove iscrizioni ma mantiene l'evento attivo in attesa dello svolgimento.
     *
     * @param event L'evento su cui operare.
     * @throws ActionNotAllowedException Se le iscrizioni non erano aperte.
     */
    void closeRegistrations(Event event);

    /**
     * Marca l'evento come completato (transizione a COMPLETED).
     * Da invocare al termine della manifestazione sportiva per storicizzare i dati.
     *
     * @param event L'evento su cui operare.
     * @throws ActionNotAllowedException Se l'evento non è stato ancora svolto o è annullato.
     */
    void completeEvent(Event event);

    /**
     * Annulla l'evento (transizione a CANCELLED).
     * L'evento viene marcato come non svolto.
     *
     * @param event L'evento su cui operare.
     * @throws ActionNotAllowedException Se l'evento è già stato completato (non si può annullare il passato).
     */
    void cancelEvent(Event event);

    /**
     * Metodo di validazione logica: verifica se è permesso effettuare un'iscrizione dato lo stato corrente dell'evento.
     * Il parametro isDraft serve perchè le registrazioni possono essere create come bozza prima dell'apertura delle iscrizioni
     *
     * @param event L'evento contesto.
     * @param isDraft true se l'iscrizione è una bozza (DRAFT), false altrimenti
     * @param isFederationManager true se la richiesta proviene da un FederationManager, false altrimenti
     * @throws ActionNotAllowedException Se l'operazione di iscrizione è vietata nello stato attuale.
     */
    void validateRegistration(Event event, boolean isDraft, boolean isFederationManager);
}