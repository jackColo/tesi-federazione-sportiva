package com.tesi.federazione.backend.dto.user;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.GenderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * DTO specifico per la visualizzazione dei dettagli di un Atleta.
 * Estende UserDTO includendo tutti i dati specifici per gli atleti.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AthleteDTO extends UserDTO {
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
}
