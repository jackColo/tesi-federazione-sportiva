package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.state.athlete.AthleteState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true) // Confronta anche i campi della classe genitore
public class Athlete extends User {
    private LocalDate birthDate;
    private Float weight;
    private Float height;
    private AffiliationStatus affiliationStatus;
    private LocalDate affiliationDate;
    private LocalDate firstAffiliationDate;
    private String medicalCertificateNumber;
    private LocalDate medicalCertificateExpireDate;
    private String clubId;

    public Athlete() {
        this.setRole(Role.ATHLETE);
    }

    @Transient
    private transient AthleteState state;

    public void approve() {
        state.next(this);
    }

    public void invalidate() {
        state.expire(this);
    }

    public boolean canOperate() {
        return state.canOperate();
    }

}
