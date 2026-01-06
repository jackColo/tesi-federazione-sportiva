package com.tesi.federazione.backend.dto.enrollment;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.CompetitionType;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EnrollmentDTO {
    private String id;
    private String eventId;
    private String clubId;
    private CompetitionType competitionType;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;

    private String athleteId;
    private String athleteFirstname;
    private String athleteLastname;
    private String athleteWeight;
    private String athleteHeight;
    private String athleteGender;
    private AffiliationStatus athleteAffiliationStatus;
    private LocalDate athleteMedicalCertificateExpireDate;
}
