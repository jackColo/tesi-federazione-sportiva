package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    @GetMapping("/all")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> allEvents = eventService.getAllEvents();
        return new ResponseEntity<>(allEvents, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable String id) {
        EventDTO eventDTO = eventService.getEventById(id);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    @GetMapping("/update-state/{id}/{newState}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<Void> updateEvent(@PathVariable String id, @PathVariable EventStatus newState) {
        eventService.updateEventState(id, newState);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<EventDTO> createNewEvent(@RequestBody CreateEventDTO event) {
        EventDTO newEvent = eventService.createEvent(event);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<EventDTO> updatedEvent(@RequestBody EventDTO newEventData) {
        EventDTO event = eventService.updateEvent(newEventData);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER','ATHLETE')")
    public ResponseEntity<EnrollmentDTO> enrollAthlete(
            @RequestBody CreateEnrollmentDTO request) {

        EnrollmentDTO newEnrollment = eventService.enrollAthlete(request);
        return new ResponseEntity<>(newEnrollment, HttpStatus.CREATED);
    }

    @GetMapping("/enroll/{id}")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER','ATHLETE')")
    public ResponseEntity<EnrollmentDTO> getEnrollment(@PathVariable String id) {
        EnrollmentDTO newEnrollment = eventService.getEnrollment(id);
        return new ResponseEntity<>(newEnrollment, HttpStatus.CREATED);
    }

    @PatchMapping("/enroll/update")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER','ATHLETE')")
    public ResponseEntity<EnrollmentDTO> updatedEnrollment(@RequestBody EnrollmentDTO request) {
        EnrollmentDTO newEnrollment = eventService.updateEnrollment(request);
        return new ResponseEntity<>(newEnrollment, HttpStatus.CREATED);
    }

    @GetMapping("/enroll-all/{eventId}")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER', 'ATHLETE')")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByEventId(@PathVariable String eventId, @RequestParam(required = false) String clubId,
                                                                       @RequestParam(required = false) String athleteId) {
        List<EnrollmentDTO> enrollments = eventService.getEnrollmentsByEventId(eventId, clubId, athleteId);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }
}