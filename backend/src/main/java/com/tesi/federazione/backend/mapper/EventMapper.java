package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.event.EventDTO;
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
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setDate(event.getDate());
        dto.setStatus(String.valueOf(event.getStatus()));
        dto.setDisciplines(event.getDisciplines());
        dto.setRegistrationOpenDate(event.getRegistrationOpenDate());
        dto.setRegistrationCloseDate(event.getRegistrationCloseDate());
        return dto;
    }
}