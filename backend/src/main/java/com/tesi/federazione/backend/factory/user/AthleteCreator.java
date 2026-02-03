package com.tesi.federazione.backend.factory.user;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.User;
import org.springframework.stereotype.Component;

/**
 * Implementazione del Creator per gli utenti con ruolo ATHLETE
 * Viene registrato nel contesto Spring con il nome "ATHLETE" (tramite @Component("ATHLETE)),
 * che corrisponde esattamente alla stringa del ruolo passata nel DTO.
 */
@Component("ATHLETE")
public class AthleteCreator implements UserCreator {

    /**
     * Crea un'istanza di Athlete popolando i dati specifici degli atleti
     * Imposta di default lo stato di affiliazione a SUBMITTED.
     */
    @Override
    public User createUser(CreateUserDTO dto) {
        Athlete athlete = new Athlete();
        athlete.setAffiliationStatus(AffiliationStatus.SUBMITTED);
        this.mapData(athlete, dto);

        return athlete;
    }

    /**
     * Aggiorno i dati dell'istanza Athlete passata in oggetto, sovrascrivendone i campi passati tramite dto
     */
    @Override
    public void updateUser(User user, CreateUserDTO dto) {
        if (user instanceof Athlete) {
            this.mapData((Athlete) user, dto);
        }
    }

    // Metodo helper per i campi comuni per evitare duplicazione codice
    private void mapData(Athlete athlete, CreateUserDTO dto) {
        athlete.setHeight(dto.getHeight());
        athlete.setWeight(dto.getWeight());
        athlete.setGender(dto.getGender());
        athlete.setBirthDate(dto.getBirthDate());
        athlete.setMedicalCertificateNumber(dto.getMedicalCertificateNumber());
        athlete.setMedicalCertificateExpireDate(dto.getMedicalCertificateExpireDate());
        athlete.setClubId(dto.getClubId());
    }
}
