package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.factory.state.EventStateFactory;
import com.tesi.federazione.backend.mapper.EnrollmentMapper;
import com.tesi.federazione.backend.mapper.EventMapper;
import com.tesi.federazione.backend.model.Enrollment;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.repository.ClubRepository;
import com.tesi.federazione.backend.repository.EnrollmentRepository;
import com.tesi.federazione.backend.repository.EventRepository;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    private final EventMapper eventMapper;
    private final EnrollmentMapper enrollmentMapper;


    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EventDTO getEventById(String id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + id + " non trovato"));
        long count = enrollmentRepository.findByEventId(id).size();
        EventDTO dto = eventMapper.toDTO(event);
        dto.setEnrolledCount(count);

        return dto;
    }

    @Override
    public void updateEventState(String id, EventStatus newState) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + id + " non trovato"));
        event.setState(EventStateFactory.getInitialState(event.getStatus()));

        switch (newState) {
            case SCHEDULED:
                event.resumeEvent();
                break;
            case COMPLETED:
                event.completeEvent();
                break;
            case CANCELLED:
                event.cancelEvent();
                break;
            case REGISTRATION_CLOSED:
                event.closeRegistrations();
                break;
            case REGISTRATION_OPEN:
                event.openRegistrations();
                break;
        }

        eventRepository.save(event);
    }

    @Override
    public EventDTO createEvent(CreateEventDTO createEventDTO) {
        Event event = new Event();
        event.setName(createEventDTO.getName());
        event.setDescription(createEventDTO.getDescription());
        event.setLocation(createEventDTO.getLocation());
        event.setDate(createEventDTO.getDate());
        event.setRegistrationOpenDate(createEventDTO.getRegistrationOpenDate());
        event.setRegistrationCloseDate(createEventDTO.getRegistrationCloseDate());
        event.setDisciplines(createEventDTO.getDisciplines());

        event.setStatus(EventStatus.SCHEDULED);

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toDTO(savedEvent);
    }

    @Override
    public EventDTO updateEvent(EventDTO eventDTO) {
        Event oldEvent = eventRepository.findById(eventDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + eventDTO.getId() + " non trovato"));

        Event newEvent = new Event();
        newEvent.setName(eventDTO.getName());
        newEvent.setDescription(eventDTO.getDescription());
        newEvent.setLocation(eventDTO.getLocation());
        newEvent.setDate(eventDTO.getDate());
        newEvent.setRegistrationOpenDate(eventDTO.getRegistrationOpenDate());
        newEvent.setRegistrationCloseDate(eventDTO.getRegistrationCloseDate());
        newEvent.setDisciplines(eventDTO.getDisciplines());

        newEvent.setStatus(oldEvent.getStatus());
        newEvent.setId(eventDTO.getId());

        Event savedEvent = eventRepository.save(newEvent);
        return eventMapper.toDTO(savedEvent);
    }

    @Override
    public EnrollmentDTO enrollAthlete(CreateEnrollmentDTO enrollmentDTO) {

        if (enrollmentRepository.existsByEventIdAndAthleteIdAndDiscipline(enrollmentDTO.getEventId(), enrollmentDTO.getAthleteId(), enrollmentDTO.getCompetitionType())) {
            throw new IllegalStateException("Atleta già iscritto all'evento " + enrollmentDTO.getEventId() + " per la disciplina " + enrollmentDTO.getCompetitionType());
        }
        clubRepository.findById(enrollmentDTO.getClubId())
                .orElseThrow(() -> new ResourceNotFoundException("Club con ID " + enrollmentDTO.getClubId() + " non trovato"));
        userRepository.findById(enrollmentDTO.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Atleta con ID " + enrollmentDTO.getClubId() + " non trovato"));


        Event event = eventRepository.findById(enrollmentDTO.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + enrollmentDTO.getEventId() + " non trovato"));

        event.setState(EventStateFactory.getInitialState(event.getStatus()));
        event.validateRegistration();

        Enrollment enrollment = new Enrollment();
        enrollment.setAthleteId(enrollmentDTO.getAthleteId());
        enrollment.setAthleteFirstname(enrollmentDTO.getAthleteFirstname());
        enrollment.setAthleteLastname(enrollmentDTO.getAthleteLastname());
        enrollment.setAthleteWeight(enrollmentDTO.getAthleteWeight());
        enrollment.setAthleteHeight(enrollmentDTO.getAthleteHeight());
        enrollment.setAthleteGender(enrollmentDTO.getAthleteGender());
        enrollment.setAthleteAffiliationStatus(enrollmentDTO.getAthleteAffiliationStatus());
        enrollment.setAthleteMedicalCertificateExpireDate(enrollmentDTO.getAthleteMedicalCertificateExpireDate());

        enrollment.setEventId(event.getId());
        enrollment.setClubId(enrollmentDTO.getClubId());
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setDiscipline(enrollmentDTO.getCompetitionType());
        if (enrollmentDTO.isDraft()) {
            enrollment.setStatus(EnrollmentStatus.DRAFT);
        } else {
            enrollment.setStatus(EnrollmentStatus.SUBMITTED);
        }

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return enrollmentMapper.toDTO(savedEnrollment);
    }

    @Override
    @Transactional
    public EnrollmentDTO updateEnrollment(EnrollmentDTO enrollmentDTO) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Iscrizione con ID " + enrollmentDTO.getId() + " non trovata"));

        Event event = eventRepository.findById(enrollment.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + enrollment.getEventId() + " non trovato"));

        event.setState(EventStateFactory.getInitialState(event.getStatus()));
        event.validateRegistration();

        if (enrollmentDTO.getCompetitionType() != null &&
                !enrollmentDTO.getCompetitionType().equals(enrollment.getDiscipline())) {

            if (enrollmentRepository.existsByEventIdAndAthleteIdAndDiscipline(enrollmentDTO.getEventId(), enrollmentDTO.getAthleteId(), enrollmentDTO.getCompetitionType())) {
                throw new IllegalStateException("Atleta già iscritto all'evento " + enrollmentDTO.getEventId() + " per la disciplina " + enrollmentDTO.getCompetitionType());
            }

            enrollment.setDiscipline(enrollmentDTO.getCompetitionType());
        }

        enrollment.setAthleteFirstname(enrollmentDTO.getAthleteFirstname());
        enrollment.setAthleteLastname(enrollmentDTO.getAthleteLastname());
        enrollment.setAthleteWeight(enrollmentDTO.getAthleteWeight());
        enrollment.setAthleteHeight(enrollmentDTO.getAthleteHeight());
        enrollment.setAthleteGender(enrollmentDTO.getAthleteGender());
        enrollment.setAthleteAffiliationStatus(enrollmentDTO.getAthleteAffiliationStatus());
        enrollment.setAthleteMedicalCertificateExpireDate(enrollmentDTO.getAthleteMedicalCertificateExpireDate());

        enrollment.setStatus(enrollmentDTO.getStatus());

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        return enrollmentMapper.toDTO(updatedEnrollment);
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByEventId(String eventId, String clubId, String athleteId) {
        List<Enrollment> enrollments = new ArrayList<>();

        if (athleteId != null) {
            enrollments = enrollmentRepository.findByEventIdAndAthleteId(eventId, athleteId);
        }
        else if (clubId != null) {
            enrollments = enrollmentRepository.findByEventIdAndClubId(eventId, clubId);
        }
        else {
            enrollments = enrollmentRepository.findByEventId(eventId);
        }

        return enrollments.stream().map(enrollmentMapper::toDTO).toList();
    }

    @Override
    public EnrollmentDTO getEnrollment(String id) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Iscrizione con id " + id + " non trovata."));
        return enrollmentMapper.toDTO(enrollment);
    }
}
