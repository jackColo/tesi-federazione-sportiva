package com.tesi.federazione.backend.dto.event;

import com.tesi.federazione.backend.model.enums.CompetitionType;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class CreateEventDTO {
    private String name;
    private String location;
    private String description;
    private LocalDate date;
    private LocalDate registrationOpenDate;
    private LocalDate registrationCloseDate;
    private Set<CompetitionType> disciplines;
}