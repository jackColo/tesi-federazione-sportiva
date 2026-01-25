package com.tesi.federazione.backend.dto.club;

import lombok.Data;

/**
 * DTO per l'aggiornamento dei dati anagrafici di un Club esistente, contenente
 * esclusivamente i campi modificabili (Nome, Codice Fiscale, Indirizzo).
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class UpdatedClubDTO {
    private String id;
    private String name;
    private String fiscalCode;
    private String legalAddress;
}
