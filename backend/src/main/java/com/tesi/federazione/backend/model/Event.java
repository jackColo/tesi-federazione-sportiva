package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.model.enums.CompetitionType;
import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.state.event.EventState;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Set;

/**
 * Entità principale che rappresenta un Evento, mappata sulla collection MongoDB "events".
 * Questa classe rappresenta il "Context" del Design Pattern "State" che viene applicato per definire lo stato dell'evento.
 * Viene mantenuto (ma non salvato a DB) un riferimento allo stato corrente tramite "EventState", sarò questi a gestire
 * l'esecuzione delle operazioni, che saranno differenti a seconda dello stato concreto in cui si trova l'evento.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
@Document(collection = "events")
public class Event {
    @Id
    private String id;

    private String name;
    private String description;
    private String location;
    private LocalDate date;

    /**
     * Utilizzo Set per l'elenco delle discipline per evitare duplicati
     */
    private Set<CompetitionType> disciplines;

    private LocalDate registrationOpenDate;
    private LocalDate registrationCloseDate;

    /**
     * Questo rappresenta lo stato salvato a DB: non deve essere salvata la logica associata all'EventState
     * ma esclusivamente l'informazione statica che indica lo stato effettivo.
     */
    private EventStatus status;

    /**
     * Riferimento allo stato corrente, popolato a runtime dall'EventStateFactory, in base al valore
     * statico dello status letto dal DB.
     * L'annotazione @Transient esclude il salvataggio del campo a DB e la dichiarazione della
     * variabile come "transient" ne evita la serializzazione.
     */
    @Transient
    private transient EventState state;

    // I seguenti metodi servono per delegare a "state" la gestione delle operazioni da
    // eseguire e che dipendono dall'effettivo stato dell'evento.

    public void resumeEvent() {
        this.state.resumeEvent(this);
    }

    public void openRegistrations() {
        this.state.openRegistrations(this);
    }
    public void closeRegistrations() {
        this.state.closeRegistrations(this);
    }
    public void completeEvent() {
        this.state.completeEvent(this);
    }
    public void cancelEvent() {
        this.state.cancelEvent(this);
    }
    public void validateRegistration(boolean isDraft, boolean isFederationManager) {
        this.state.validateRegistration(this, isDraft, isFederationManager);
    }

}
