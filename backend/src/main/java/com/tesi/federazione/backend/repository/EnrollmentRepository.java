package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.Enrollment;
import com.tesi.federazione.backend.model.enums.CompetitionType;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository per il salvataggio degli oggetti della classe Enrollment nel DB Mongo.
 * Le CRUD di base non sono dichiarate in quanto definite automaticamente da Spring Data.
 */
public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {
    /**
     * Ricerca di tutte le iscrizioni relative ad uno specifico evento.
     * @param eventId Id dell'evento
     * @return List<Enrollment> Elenco di tutte le iscrizioni presenti a DB per l'evento richiesto
     */
    List<Enrollment> findByEventId(String eventId);

    /**
     * Ricerca di tutte le iscrizioni ACCETTATE relative ad uno specifico evento.
     * @param eventId Id dell'evento
     * @return List<Enrollment> Elenco di tutte le iscrizioni presenti a DB per l'evento richiesto
     */
    List<Enrollment> findByEventIdAndStatus(String eventId, EnrollmentStatus status);

    /**
     * Ricerca di tutte le iscrizioni relative ad uno specifico club per uno specifico evento.
     * @param eventId Id dell'evento
     * @param clubId Id del club
     * @return List<Enrollment> Elenco di tutte le iscrizioni presenti a DB relative a club ed evento richiesti.
     */
    List<Enrollment> findByEventIdAndClubId(String eventId, String clubId);

    /**
     * Ricerca di tutte le iscrizioni relative ad uno specifico atleta per uno specifico evento.
     * @param eventId Id dell'evento
     * @param athleteId Id dell'atleta
     * @return List<Enrollment> Elenco di tutte le iscrizioni presenti a DB relative a atleta ed evento richiesti.
     */
    List<Enrollment> findByEventIdAndAthleteId(String eventId, String athleteId);

    /**
     * Verifica circa la presenza dell'iscrizione di un atleta ad uno specifico evento e per una specifica disciplina
     * @param eventId Id dell'evento
     * @param athleteId Id dell'atleta
     * @param competitionType Enum relativo alla disciplina da verificare
     * @return boolean true se presente, false altrimenti
     */
    boolean existsByEventIdAndAthleteIdAndDiscipline(String eventId, String athleteId, CompetitionType competitionType);
}