package com.tesi.federazione.backend.model;
import com.tesi.federazione.backend.enums.AffiliationStatus;
import com.tesi.federazione.backend.state.club.ClubState;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
@Document(collection = "clubs")
public class Club {

    @Id
    private String id;
    private String name;
    private String fiscalCode;
    private String legalAddress;
    private AffiliationStatus affiliationStatus;
    private LocalDate affiliationDate;
    private LocalDate firstAffiliationDate;

    @DBRef
    private ArrayList<ClubManager> managers;
    @DBRef
    private ArrayList<Athlete> athletes;

    @Transient
    private transient ClubState state;

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