package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.ClubDTO;
import com.tesi.federazione.backend.dto.CreateUserDTO;
import com.tesi.federazione.backend.dto.UserDTO;
import com.tesi.federazione.backend.enums.Role;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class ClubMapper {

    public ClubDTO toDTO(Club club) {
        if (club == null) {
            return null;
        }

        ClubDTO dto = new ClubDTO();
        dto.setId(club.getId());
        dto.setName(club.getName());
        dto.setManagers(club.getManagers().stream()
                .map(ClubManager::getId)
                .collect(Collectors.toCollection(ArrayList::new)));

        return dto;
    }

}