package com.tesi.federazione.backend.dto.club;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.ArrayList;

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
