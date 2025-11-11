package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.enums.CompetitionType;
import com.tesi.federazione.backend.enums.EventStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Set;

@Data
@Document(collation = "events")
public class Event {
    @Id
    private String id;

    private String name;
    private String description;
    private String location;
    private LocalDate date;
    private Set<CompetitionType> disciplines; // Utilizzo Set per evitare duplicati

    private LocalDate registrationOpenDate;
    private LocalDate registrationCloseDate;

    private EventStatus status;
}
