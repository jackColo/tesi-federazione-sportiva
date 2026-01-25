package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.model.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Specializzazione dell'entità User che rappresenta un Federation Manager.
 * Possiede i privilegi più elevati nel sistema, ha accesso completo a tutti i dati.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
@EqualsAndHashCode(callSuper = true) // Confronta anche i campi della classe genitore
public class FederationManager extends User {

    public FederationManager() {
        this.setRole(Role.FEDERATION_MANAGER);
    }
}
