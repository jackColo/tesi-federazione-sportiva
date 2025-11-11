package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.CreateEventDTO;
import com.tesi.federazione.backend.dto.EventDTO;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.service.EventService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDTO> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping
    public EventDTO createNewEvent(@RequestBody CreateEventDTO event) {
        return eventService.createEvent(event);
    }
}