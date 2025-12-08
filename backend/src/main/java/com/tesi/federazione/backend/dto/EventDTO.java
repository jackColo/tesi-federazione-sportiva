package com.tesi.federazione.backend.dto;

import com.tesi.federazione.backend.enums.CompetitionType;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.Set;

@Data
public class EventDTO {
    @Id
    private String id;
    private String name;
    private String location;
    private LocalDate date;
    private LocalDate registrationOpenDate;
    private LocalDate registrationCloseDate;
    private String status;
    private Set<CompetitionType> disciplines;
}
