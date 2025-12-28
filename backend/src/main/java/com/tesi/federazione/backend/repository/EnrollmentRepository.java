package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.Enrollment;
import com.tesi.federazione.backend.model.enums.CompetitionType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {
    // Le CRUD di base vengono definite automaticamente da Spring Data

    List<Enrollment> findByEventId(String eventId);

    List<Enrollment> findByAthleteId(String athleteId);

    List<Enrollment> findByEnrollingClubIdAndEventId(String enrollingClubId, String eventId);
    boolean existsByEventAndAthleteAndCompetitionType(String eventId, String athleteId, CompetitionType competitionType);
}