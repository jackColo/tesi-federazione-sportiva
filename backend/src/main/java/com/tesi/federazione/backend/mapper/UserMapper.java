package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.dto.user.ClubManagerDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import org.springframework.stereotype.Component;


/**
 * Classe utility per mappare i gli oggetti User nei formati DTO
 * Gestisce il polimorfismo degli utenti restituendo il DTO specifico
 * in base al tipo concreto dell'entità.
 */
@Component
public class UserMapper {

    /**
     * Metodo principale di conversione, analizza il tipo dinamico dell'istanza
     * User passata e delega la mappaturaal metodo specifico, restituendo il
     * DTO più dettagliato possibile.
     *
     * @param user L'entità da convertire.
     * @return UserDTO (o una sua sottoclasse) popolato.
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        // Controllo di tipo per delegare alla mappatura specifica
        if (user instanceof ClubManager) {
            return toClubManagerDTO((ClubManager) user);
        }

        else if (user instanceof Athlete) {
            return toAthleteDTO((Athlete) user);
        }

        // Fallback per utenti generici (come FederationManager che al
        // momento corrisponde allo User)
        UserDTO dto = new UserDTO();
        mapBaseFields(user, dto);
        return dto;
    }

    /**
     * Mappa specifica per i ClubManager.
     * Aggiunge il riferimento al club gestito.
     */
    private ClubManagerDTO toClubManagerDTO(ClubManager user) {
        ClubManagerDTO dto = new ClubManagerDTO();
        mapBaseFields(user, dto);
        dto.setClubId(user.getManagedClub());
        return dto;
    }

    /**
     * Mappa specifica per gli Athlete.
     * Aggiunge tutti i dati specifici degli atleti.
     */
    private AthleteDTO toAthleteDTO(Athlete user) {
        AthleteDTO dto = new AthleteDTO();
        mapBaseFields(user, dto);

        // Dati club e affiliazione
        dto.setClubId(user.getClubId());
        dto.setAffiliationStatus(user.getAffiliationStatus());
        dto.setAffiliationDate(user.getAffiliationDate());
        dto.setFirstAffiliationDate(user.getFirstAffiliationDate());

        // Dati personali
        dto.setBirthDate(user.getBirthDate());
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setGender(user.getGender());

        // Dati medici
        dto.setMedicalCertificateNumber(user.getMedicalCertificateNumber());
        dto.setMedicalCertificateExpireDate(user.getMedicalCertificateExpireDate());
        return dto;
    }

    /**
     * Metodo helper per mappare da entity a DTO i campi base, comuni a tutti gli utenti
     * Evita la duplicazione di codice nei metodi specifici.
     *
     * @param user Entità
     * @param dto DTO destinazione (può essere UserDTO o sue sottoclassi).
     */
    private void mapBaseFields(User user, UserDTO dto) {
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(String.valueOf(user.getRole()));
    }

}