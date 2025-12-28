package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.model.Athlete;
import org.springframework.stereotype.Component;

@Component
public class AthleteMapper {

    public AthleteDTO toDTO(Athlete user) {
        if (user == null) {
            return null;
        }

        AthleteDTO dto = new AthleteDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(Role.ATHLETE.name());
        return dto;
    }
}