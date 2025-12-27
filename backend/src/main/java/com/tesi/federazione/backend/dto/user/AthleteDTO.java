package com.tesi.federazione.backend.dto.user;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class AthleteDTO extends UserDTO {
    private LocalDate birthDate;
    private Float weight;
    private Float height;
    private AffiliationStatus affiliationStatus;
    private LocalDate affiliationDate;
    private LocalDate firstAffiliationDate;
    private String medicalCertificateNumber;
    private LocalDate medicalCertificateExpireDate;
    private String clubId;
}
