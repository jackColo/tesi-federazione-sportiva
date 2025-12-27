package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.model.enums.CompetitionType;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "enrollments")
public class Enrollment {
    @Id
    private String id;

    @DBRef
    private Event event;

    @DBRef
    private Athlete athlete;
    private CompetitionType discipline;

    @DBRef
    private Club enrollingClub;

    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;
}
