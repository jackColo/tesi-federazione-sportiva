package com.tesi.federazione.backend.dto.user;

import com.tesi.federazione.backend.enums.AffiliationStatus;
import lombok.Data;

@Data
public class CreateClubDTO {
    private String name;
    private String fiscalCode;
    private String legalAddress;
    private AffiliationStatus affiliationStatus;
    private CreateUserDTO manager ;
}
