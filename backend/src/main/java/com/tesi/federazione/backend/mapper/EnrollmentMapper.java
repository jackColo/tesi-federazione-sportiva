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
        dto.setStatus(enrollment.getStatus());
        dto.setDiscipline(enrollment.getDiscipline());

        if (enrollment.getEvent() != null) {
            dto.setEventDate(enrollment.getEvent().getDate());
            dto.setEventName(enrollment.getEvent().getName());
        }

        if (enrollment.getAthlete() != null) {
            dto.setAthleteName(enrollment.getAthlete().getFirstName());
            dto.setAthleteSurname(enrollment.getAthlete().getLastName());
        }

        if (enrollment.getEnrollingClub() != null) {
            dto.setClubName(enrollment.getEnrollingClub().getName());
        }

        return dto;
    }
}
