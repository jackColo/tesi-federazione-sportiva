package com.tesi.federazione.backend.model;
import com.tesi.federazione.backend.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClubManager extends User {

    @DBRef
    private Club managedClub;

    public ClubManager() {
        this.setRole(Role.CLUB_MANAGER);
    }
}