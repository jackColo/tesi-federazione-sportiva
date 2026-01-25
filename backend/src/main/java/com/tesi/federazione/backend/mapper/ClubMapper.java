package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.ClubManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Classe utility per mappare i gli oggetti Club nei formati DTO
 */
@Component
public class ClubMapper {

    /**
     * Metodo per mappare un club da entità a DTO
     * @param club Club come oggetto di tipo Club
     * @return ClubDTO Club come oggetto di tipo ClubDTO
     */
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