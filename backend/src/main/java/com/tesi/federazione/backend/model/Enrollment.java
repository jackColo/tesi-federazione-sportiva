package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.CompetitionType;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entità che rappresenta la singola iscrizione di un atleta a una specifica disciplina
 * per uno specifico evento, mappata sulla collection MongoDB "enrollments".
 * Questa classe non si limita a salvare l'ID dell'atleta e la disciplina scelta per l'evento,
 * salva anche tutti i dati dell'atleta così come sono definiti al momento dell'iscrizione (Snapshot)
 * I questo modo viene garantita l'integrità dei dati: eventuali modifiche future alla scheda
 * dell'atleta non alterano i dati relativi a eventi passati.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
@Document(collection = "enrollments")
public class Enrollment {
    @Id
    private String id;
    private String eventId;
    private String clubId;
    private String athleteId;

    private LocalDateTime enrollmentDate;
    private CompetitionType discipline;
    private EnrollmentStatus status;

    // I seguenti campi rappresentano lo snapshot di tutti i dati dell'atleta al momento dell'iscrizione
    private String athleteClubName;
    private String athleteFirstname;
    private String athleteLastname;
    private String athleteWeight;
    private String athleteHeight;
    private String athleteGender;
    private AffiliationStatus athleteAffiliationStatus;
    private LocalDate athleteMedicalCertificateExpireDate;
}
