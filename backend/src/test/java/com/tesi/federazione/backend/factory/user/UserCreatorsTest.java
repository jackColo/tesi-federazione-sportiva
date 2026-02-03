package com.tesi.federazione.backend.factory.user;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.FederationManager;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.GenderEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UserCreatorsTest {

    @Test
    @DisplayName("Test per AthleteCreator: Mappa correttamente tutti i campi specifici")
    void athleteCreatorTest() {
        AthleteCreator creator = new AthleteCreator();
        Float height = 180.5f;
        Float weight = 90.5f;

        CreateUserDTO dto = new CreateUserDTO();
        dto.setHeight(height);
        dto.setWeight(weight);
        dto.setGender(GenderEnum.M);
        dto.setBirthDate(LocalDate.of(1995, 5, 20));
        dto.setMedicalCertificateNumber("12345678");
        dto.setMedicalCertificateExpireDate(LocalDate.of(2025, 5, 20));
        dto.setClubId("clubId");

        User result = creator.createUser(dto);

        assertInstanceOf(Athlete.class, result);
        Athlete athlete = (Athlete) result;

        assertEquals(height, athlete.getHeight());
        assertEquals(weight, athlete.getWeight());
        assertEquals(GenderEnum.M, athlete.getGender());
        assertEquals(LocalDate.of(1995, 5, 20), athlete.getBirthDate());
        assertEquals("12345678", athlete.getMedicalCertificateNumber());
        assertEquals("clubId", athlete.getClubId());

        assertEquals(AffiliationStatus.SUBMITTED, athlete.getAffiliationStatus());
    }

    @Test
    @DisplayName("Test per AthleteCreator (UPDATE): Aggiorna i dati dell'atleta esistente")
    void athleteCreatorUpdateTest() {
        AthleteCreator creator = new AthleteCreator();
        Float height = 180.5f;
        Float weight = 90.5f;

        Athlete existingAthlete = new Athlete();
        existingAthlete.setHeight(height);
        existingAthlete.setWeight(weight);
        existingAthlete.setClubId("clubId");
        existingAthlete.setGender(GenderEnum.F);
        existingAthlete.setAffiliationStatus(AffiliationStatus.ACCEPTED);
        existingAthlete.setAffiliationDate(LocalDate.of(2025, 5, 20));
        existingAthlete.setFirstAffiliationDate(LocalDate.of(2024, 5, 20));

        CreateUserDTO updateDto = new CreateUserDTO();
        Float newHeight = 185.5f;
        Float newWeight = 85.0f;

        updateDto.setHeight(newHeight);
        updateDto.setWeight(newWeight);
        updateDto.setGender(GenderEnum.F);
        updateDto.setBirthDate(LocalDate.of(1990, 1, 1));
        updateDto.setClubId("clubId");
        updateDto.setMedicalCertificateNumber("newMedicalCertificateNumber");

        creator.updateUser(existingAthlete, updateDto);

        // Verifico che i campi siano cambiati
        assertEquals(newHeight, existingAthlete.getHeight());
        assertEquals(newWeight, existingAthlete.getWeight());
        assertEquals("newMedicalCertificateNumber", existingAthlete.getMedicalCertificateNumber());
        assertEquals(LocalDate.of(1990, 1, 1), existingAthlete.getBirthDate());

        // Verifico che i campi che non variano siano rimasti uguali
        assertEquals(AffiliationStatus.ACCEPTED,  existingAthlete.getAffiliationStatus());
        assertEquals(LocalDate.of(2025, 5, 20), existingAthlete.getAffiliationDate());
        assertEquals(LocalDate.of(2024, 5, 20), existingAthlete.getFirstAffiliationDate());
    }

    @Test
    @DisplayName("Test per ClubManagerCreator: Mappa correttamente il managedClub")
    void testClubManagerCreator() {
        ClubManagerCreator creator = new ClubManagerCreator();

        CreateUserDTO dto = new CreateUserDTO();
        dto.setClubId("clubId");

        User result = creator.createUser(dto);

        assertInstanceOf(ClubManager.class, result);
        ClubManager manager = (ClubManager) result;

        assertEquals("clubId", manager.getManagedClub());
    }

    @Test
    @DisplayName("Test per FederationManagerCreator: crea correttamente lo user come istanza di FederationManager")
    void testFederationManagerCreator() {
        FederationManagerCreator creator = new FederationManagerCreator();
        CreateUserDTO dto = new CreateUserDTO();

        User result = creator.createUser(dto);

        assertInstanceOf(FederationManager.class, result);
    }
}