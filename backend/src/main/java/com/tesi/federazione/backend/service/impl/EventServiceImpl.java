package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.factory.state.EventStateFactory;
import com.tesi.federazione.backend.mapper.EnrollmentMapper;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.Enrollment;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.mapper.EventMapper;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.repository.ClubRepository;
import com.tesi.federazione.backend.repository.EnrollmentRepository;
import com.tesi.federazione.backend.repository.EventRepository;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.EventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;


    private final EventMapper eventMapper;
    private final EnrollmentMapper enrollmentMapper;

    public EventServiceImpl(EventRepository eventRepository, EnrollmentRepository enrollmentRepository, ClubRepository clubRepository, UserRepository userRepository, EventMapper eventMapper, EnrollmentMapper enrollmentMapper) {
        this.eventRepository = eventRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.enrollmentMapper = enrollmentMapper;
    }


    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EventDTO createEvent(CreateEventDTO createEventDTO) {
        Event event = eventMapper.toEntity(createEventDTO);

        event.setStatus(EventStatus.SCHEDULED);

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toDTO(savedEvent);
    }

    @Override
    public EnrollmentDTO enrollAthlete(CreateEnrollmentDTO enrollmentDTO) {

        if (enrollmentRepository.existsByEventAndAthleteAndCompetitionType(enrollmentDTO.getEventId(), enrollmentDTO.getAthleteId(), enrollmentDTO.getCompetitionType())) {
            throw new IllegalStateException("Atleta già iscritto all'evento " + enrollmentDTO.getEventId() + " per la disciplina " + enrollmentDTO.getCompetitionType());
        }

        Event event = eventRepository.findById(enrollmentDTO.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + enrollmentDTO.getEventId() + " non trovato"));
        Club club = clubRepository.findById(enrollmentDTO.getClubId())
                .orElseThrow(() -> new ResourceNotFoundException("Club con ID " + enrollmentDTO.getClubId() + " non trovato"));
        Athlete athlete = (Athlete) userRepository.findById(enrollmentDTO.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Atleta con ID " + enrollmentDTO.getClubId() + " non trovato"));

        event.setState(EventStateFactory.getInitialState(event.getStatus()));
        event.validateRegistration();

        Enrollment enrollment = new Enrollment();
        enrollment.setAthlete(athlete);
        enrollment.setEvent(event);
        enrollment.setEnrollingClub(club);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setDiscipline(enrollmentDTO.getCompetitionType());
        enrollment.setStatus(EnrollmentStatus.SUBMITTED);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return enrollmentMapper.toDTO(savedEnrollment);
    }
}
