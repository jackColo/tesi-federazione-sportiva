package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.model.enums.EventStatus;

import java.util.List;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO getEventById(String id);
    EventDTO createEvent(CreateEventDTO event);
    EventDTO updateEvent(EventDTO eventDTO);
    void updateEventState(String id, EventStatus newState);

    EnrollmentDTO enrollAthlete(CreateEnrollmentDTO enrollment);
    EnrollmentDTO updateEnrollment(EnrollmentDTO enrollment);
    List<EnrollmentDTO> getEnrollmentsByEventId(String eventId, String clubId, String athleteId);
    EnrollmentDTO getEnrollment(String id);
}
