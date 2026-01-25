package com.tesi.federazione.backend.dto.club;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import lombok.Data;

/**
 * DTO per la registrazione di un nuovo Club.
 * Questo oggetto permette di creare contemporaneamente l'entità Club e il suo primo ClubManager (tramite il campo "manager").
 * Questo garantisce che un club abbia sempre almeno un responsabile associato.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class CreateClubDTO {
    private String name;
    private String fiscalCode;
    private String legalAddress;
    private AffiliationStatus affiliationStatus;
    private CreateUserDTO manager ;
}
