package com.tesi.federazione.backend.model;
import com.tesi.federazione.backend.enums.AffiliationStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
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
    private AffiliationStatus affiliationStatus;
    private LocalDate affiliationDate;
    private LocalDate firstAffiliationDate;

    @DBRef
    private ArrayList<ClubManager> managers;
    @DBRef
    private ArrayList<Athlete> athletes;
}