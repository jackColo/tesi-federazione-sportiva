package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FederationManager extends User {

    public FederationManager() {
        this.setRole(Role.FEDERATION_MANAGER);
    }
}
