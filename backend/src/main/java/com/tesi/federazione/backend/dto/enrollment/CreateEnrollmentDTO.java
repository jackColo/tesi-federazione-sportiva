package com.tesi.federazione.backend.dto.enrollment;

import com.tesi.federazione.backend.model.enums.CompetitionType;
import lombok.Data;

@Data
public class CreateEnrollmentDTO {
    private String clubId;
    private String athleteId;
    private String eventId;
    private CompetitionType competitionType;
}
