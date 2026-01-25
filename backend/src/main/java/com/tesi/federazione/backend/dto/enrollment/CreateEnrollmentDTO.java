package com.tesi.federazione.backend.dto.enrollment;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.CompetitionType;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO di input per la creazione o modifica di un'iscrizione.
 * Il campo booleano isDraft è un flag di controllo necessario per il Pattern State:
 * determina se l'iscrizione deve nascere come bozza (DRAFT) o se debba essere inviata
 * definitivamente (SUBMITTED) per l'approvazione: in base a questo campo le operazioni
 * d'iscrizione e modifica concesse sono differenti e a determinarlo sono gli stati concreti
 * definite tramite il pattern state.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
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

    /**
     * Flag che indica l'intenzione dell'utente:
     * true = Salva come Bozza (dovrà essere verificata dal club manager per l'invio effettivo)
     * false = Invia Iscrizione (la richiesta è corretta e può essere inviata definitivamente).
     */
    private boolean isDraft;
}
