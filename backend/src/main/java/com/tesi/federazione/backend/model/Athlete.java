package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.GenderEnum;
import com.tesi.federazione.backend.model.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Specializzazione dell'entità User che rappresenta un Atleta.
 * Utente di base del sistema, ha permessi di accesso ai dati limitati alla sua utenza.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
@EqualsAndHashCode(callSuper = true) // Confronta anche i campi della classe genitore
public class Athlete extends User {
    private LocalDate birthDate;
    private Float weight;
    private Float height;
    private GenderEnum gender;
    private AffiliationStatus affiliationStatus;
    private LocalDate affiliationDate;
    private LocalDate firstAffiliationDate;
    private String medicalCertificateNumber;
    private LocalDate medicalCertificateExpireDate;
    private String clubId;

    /**
     * Definisco di default il ruolo degli atleti
     */
    public Athlete() {
        this.setRole(Role.ATHLETE);
    }

}
