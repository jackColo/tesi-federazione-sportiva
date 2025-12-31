package com.tesi.federazione.backend.factory.user;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.User;
import org.springframework.stereotype.Component;

@Component("ATHLETE")
public class AthleteCreator implements UserCreator {
    @Override
    public User createUser(CreateUserDTO dto) {
        Athlete athlete = new Athlete();
        athlete.setHeight(dto.getHeight());
        athlete.setWeight(dto.getWeight());
        athlete.setWeight(dto.getWeight());
        athlete.setGender(dto.getGender());
        athlete.setBirthDate(dto.getBirthDate());
        athlete.setMedicalCertificateNumber(dto.getMedicalCertificateNumber());
        athlete.setMedicalCertificateExpireDate(dto.getMedicalCertificateExpireDate());
        athlete.setClubId(dto.getClubId());
        athlete.setAffiliationStatus(AffiliationStatus.SUBMITTED);

        return athlete;
    }
}
