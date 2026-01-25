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

@Data
@Document(collection = "events")
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

    @Transient
    private transient EventState state;

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
