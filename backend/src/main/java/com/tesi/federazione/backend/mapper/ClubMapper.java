package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.ClubManager;
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
        dto.setFiscalCode(club.getFiscalCode());
        dto.setLegalAddress(club.getLegalAddress());
        dto.setAffiliationDate(club.getAffiliationDate());
        dto.setFirstAffiliationDate(club.getFirstAffiliationDate());
        dto.setAffiliationStatus(club.getAffiliationStatus());
        dto.setManagers(club.getManagers().stream()
                .map(ClubManager::getId)
                .collect(Collectors.toCollection(ArrayList::new)));
        if (club.getAthletes() != null) {
            dto.setAthletes(club.getAthletes().stream()
                .map(Athlete::getId)
                .collect(Collectors.toCollection(ArrayList::new)));
        } else {
            dto.setAthletes(new ArrayList<>());
        }

        return dto;
    }

}