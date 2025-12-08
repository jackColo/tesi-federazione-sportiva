package com.tesi.federazione.backend.dto;

import com.tesi.federazione.backend.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.ClubManager;
import lombok.Data;

import java.util.ArrayList;

@Data
public class CreateClubDTO {
    private String name;
    private String fiscalCode;
    private String legalAddress;
    private AffiliationStatus affiliationStatus;
    private ArrayList<ClubManager> managers ;
}
