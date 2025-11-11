package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.enums.AffiliationStatus;
import com.tesi.federazione.backend.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true) // Confronta anche i campi della classe genitore
public class Athlete extends User {
    private LocalDate birthDate;
    private Float weight;
    private String experience;
    private AffiliationStatus affiliationStatus;
    private LocalDate affiliationDate;
    private LocalDate firstAffiliationDate;

    @DBRef
    private Club club;

    public Athlete() {
        this.setRole(Role.ATHLETE);
    }

}
