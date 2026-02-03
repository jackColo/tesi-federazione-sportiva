package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.model.Enrollment;
import org.springframework.stereotype.Component;

/**
 * Classe utility per mappare i gli oggetti Enrollment nei formati DTO
 */
@Component
public class EnrollmentMapper {

    /**
     * Metodo per mappare un iscrizione da entità a DTO
     * @param enrollment Iscrizione come oggetto Enrollment
     * @return EnrollmentDTO Iscrizione come oggetto EnrollmentDTO
     */
    public EnrollmentDTO toDTO(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setEventId(enrollment.getEventId());
        dto.setClubId(enrollment.getClubId());
        dto.setStatus(enrollment.getStatus());
        dto.setCompetitionType(enrollment.getDiscipline());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());

        dto.setAthleteId(enrollment.getAthleteId());
        dto.setAthleteClubName(enrollment.getAthleteClubName());
        dto.setAthleteFirstname(enrollment.getAthleteFirstname());
        dto.setAthleteLastname(enrollment.getAthleteLastname());
        dto.setAthleteWeight(enrollment.getAthleteWeight());
        dto.setAthleteHeight(enrollment.getAthleteHeight());
        dto.setAthleteGender(enrollment.getAthleteGender());
        dto.setAthleteAffiliationStatus(enrollment.getAthleteAffiliationStatus());
        dto.setAthleteMedicalCertificateExpireDate(enrollment.getAthleteMedicalCertificateExpireDate());


        return dto;
    }
}
