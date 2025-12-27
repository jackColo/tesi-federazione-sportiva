package com.tesi.federazione.backend.model;
import com.tesi.federazione.backend.model.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClubManager extends User {

    private String managedClub;

    public ClubManager() {
        this.setRole(Role.CLUB_MANAGER);
    }
}