package com.tesi.federazione.backend.dto.enrollment;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.CompetitionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateEnrollmentDTO {
    private String clubId;
    private String athleteId;
    private String athleteFirstname;
    private String athleteLastname;
    private String athleteWeight;
    private String athleteHeight;
    private String athleteGender;
    private AffiliationStatus athleteAffiliationStatus;
    private LocalDate athleteMedicalCertificateExpireDate;
    private String eventId;
    private CompetitionType competitionType;
    private boolean isDraft;
}
