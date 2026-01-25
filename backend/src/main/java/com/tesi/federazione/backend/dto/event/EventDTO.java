package com.tesi.federazione.backend.dto.event;

import com.tesi.federazione.backend.model.enums.CompetitionType;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO di output per la visualizzazione dei dettagli di un evento.
 * Oltre ai dati salvati su DB, arricchisce la risposta con il numero iscritti, calcolato alla creazione dell'oggetto,
 * in base alle iscrizioni associate presenti a DB.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class EventDTO {
    private String id;
    private String name;
    private String description;
    private String location;
    private LocalDate date;
    private LocalDate registrationOpenDate;
    private LocalDate registrationCloseDate;
    private String status;
    private Set<CompetitionType> disciplines;

    // Parametri non salvati a db
    private Long enrolledCount;
}
