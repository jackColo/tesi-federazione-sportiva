package com.tesi.federazione.backend.model;
import com.tesi.federazione.backend.model.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Specializzazione dell'entità User che rappresenta un Club Manager.
 * È responsabile della gestione dell'anagrafica del club e dei suoi atleti e dell'iscrizione degli atleti alle competizioni.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
@EqualsAndHashCode(callSuper = true) // Confronta anche i campi della classe genitore
public class ClubManager extends User {

    private String managedClub;

    public ClubManager() {
        this.setRole(Role.CLUB_MANAGER);
    }
}