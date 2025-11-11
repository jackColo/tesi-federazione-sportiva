package com.tesi.federazione.backend.dto;

import com.tesi.federazione.backend.enums.CompetitionType;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class EventDTO {
    private String id;
    private String name;
    private String location;
    private LocalDate date;
    private String status;
    private Set<CompetitionType> disciplines;
}
