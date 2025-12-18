package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.EventDTO;
import com.tesi.federazione.backend.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }

        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setLocation(event.getLocation());
        dto.setDate(event.getDate());
        dto.setStatus(String.valueOf(event.getStatus()));
        dto.setDisciplines(event.getDisciplines());
        return dto;
    }

    public Event toEntity(CreateEventDTO dto) {
        if (dto == null) {
            return null;
        }

        Event event = new Event();
        event.setName(dto.getName());
        event.setLocation(dto.getLocation());
        event.setDate(dto.getDate());
        event.setRegistrationOpenDate(dto.getRegistrationOpenDate());
        event.setRegistrationCloseDate(dto.getRegistrationCloseDate());
        event.setDisciplines(dto.getDisciplines());
        return event;
    }
}