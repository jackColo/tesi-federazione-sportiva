package com.tesi.federazione.backend.dto.club;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * DTO per la visualizzazione dei dettagli di un Club.
 * Qui le liste di Manager e Atleti sono convertite in liste di String, in modo che al client venga
 * passato esclusivamente lo stretto necessario, riducendo la dimension del JSON.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class ClubDTO {
    private String id;
    private String name;
    private String fiscalCode;
    private String legalAddress;
    private AffiliationStatus affiliationStatus;
    private ArrayList<String> managers ;
    private LocalDate affiliationDate;
    private LocalDate firstAffiliationDate;
    private ArrayList<String> athletes;
}
