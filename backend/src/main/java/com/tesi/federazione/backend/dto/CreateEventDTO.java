package com.tesi.federazione.backend.dto;

import com.tesi.federazione.backend.enums.CompetitionType;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class CreateEventDTO {
    private String name;
    private String location;
    private LocalDate date;
    private LocalDate registrationOpenDate;
    private LocalDate registrationCloseDate;
    private Set<CompetitionType> disciplines;
}