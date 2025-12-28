package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> allEvents = eventService.getAllEvents();
        return new ResponseEntity<>(allEvents, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<EventDTO> createNewEvent(@RequestBody CreateEventDTO event) {
        EventDTO newEvent = eventService.createEvent(event);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER')")
    public ResponseEntity<EnrollmentDTO> enrollAthlete(
            @RequestBody CreateEnrollmentDTO request) {

        EnrollmentDTO newEnrollment = eventService.enrollAthlete(request);
        return new ResponseEntity<>(newEnrollment, HttpStatus.CREATED);
    }
}