package com.tesi.federazione.backend.dto.enrollment;

import com.tesi.federazione.backend.model.enums.CompetitionType;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnrollmentDTO {
    private String id;
    private String eventName;
    private LocalDate eventDate;

    private String athleteName;
    private String athleteSurname;

    private String clubName;

    private CompetitionType discipline;
    private String category;
    private EnrollmentStatus status;
}
