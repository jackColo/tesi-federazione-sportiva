package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.EventDTO;

import java.util.List;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO createEvent(CreateEventDTO event);
}
