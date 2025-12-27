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

    public Athlete toEntity(AthleteDTO dto) {
        if (dto == null) {
            return null;
        }

        Athlete user = new Athlete();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setRole(Role.ATHLETE);
        user.setAffiliationStatus(dto.getAffiliationStatus());
        user.setWeight(dto.getWeight());
        user.setHeight(dto.getHeight());
        user.setAffiliationDate(dto.getAffiliationDate());
        user.setMedicalCertificateNumber(dto.getMedicalCertificateNumber());
        user.setMedicalCertificateExpireDate(dto.getMedicalCertificateExpireDate());
        user.setClubId(dto.getClubId());

        return user;
    }
}