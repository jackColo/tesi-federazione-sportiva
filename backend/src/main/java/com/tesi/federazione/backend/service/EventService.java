package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO getEventById(String id);
    EventDTO createEvent(CreateEventDTO event);
    EnrollmentDTO enrollAthlete(CreateEnrollmentDTO enrollment);
}
