package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.dto.user.ClubManagerDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        if (user instanceof ClubManager) {
            return toClubManagerDTO((ClubManager) user);
        }

        else if (user instanceof Athlete) {
            return toAthleteDTO((Athlete) user);
        }

        UserDTO dto = new UserDTO();
        mapBaseFields(user, dto);
        return dto;
    }

    private ClubManagerDTO toClubManagerDTO(ClubManager user) {
        ClubManagerDTO dto = new ClubManagerDTO();
        mapBaseFields(user, dto);
        dto.setClubId(user.getManagedClub());
        return dto;
    }

    private AthleteDTO toAthleteDTO(Athlete user) {
        AthleteDTO dto = new AthleteDTO();
        mapBaseFields(user, dto);
        dto.setClubId(user.getClubId());
        dto.setBirthDate(user.getBirthDate());
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setGender(user.getGender());
        dto.setAffiliationStatus(user.getAffiliationStatus());
        dto.setAffiliationDate(user.getAffiliationDate());
        dto.setFirstAffiliationDate(user.getFirstAffiliationDate());
        dto.setMedicalCertificateNumber(user.getMedicalCertificateNumber());
        dto.setMedicalCertificateExpireDate(user.getMedicalCertificateExpireDate());
        return dto;
    }


    private void mapBaseFields(User user, UserDTO dto) {
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(String.valueOf(user.getRole()));
    }

}