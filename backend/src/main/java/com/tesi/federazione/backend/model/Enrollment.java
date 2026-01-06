package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.CompetitionType;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "enrollments")
public class Enrollment {
    @Id
    private String id;
    private String eventId;
    private String clubId;

    private LocalDateTime enrollmentDate;
    private CompetitionType discipline;
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
