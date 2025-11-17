package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.CreateEventDTO;
import com.tesi.federazione.backend.dto.EventDTO;
import com.tesi.federazione.backend.enums.EventStatus;
import com.tesi.federazione.backend.mapper.EventMapper;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.repository.EventRepository;
import com.tesi.federazione.backend.service.EventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAuthority(T(com.tesi.federazione.backend.enums.Role).FEDERATION_MANAGER.name())")
    public EventDTO createEvent(CreateEventDTO createEventDTO) {
        Event event = eventMapper.toEntity(createEventDTO);

        event.setStatus(EventStatus.SCHEDULED);

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toDTO(savedEvent);
    }
}
