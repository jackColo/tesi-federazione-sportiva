package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.state.event.EventState;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO getEventById(String id);
    EventDTO createEvent(CreateEventDTO event);
    EventDTO updateEvent(EventDTO eventDTO);
    EnrollmentDTO enrollAthlete(CreateEnrollmentDTO enrollment);
    void updateEventState(String id, EventStatus newState);
}
