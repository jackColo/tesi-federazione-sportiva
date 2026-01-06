package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.model.Enrollment;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {

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
