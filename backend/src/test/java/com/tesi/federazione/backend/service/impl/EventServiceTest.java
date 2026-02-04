package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.exception.ResourceConflictException;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.exception.UnauthorizedException;
import com.tesi.federazione.backend.mapper.EnrollmentMapper;
import com.tesi.federazione.backend.mapper.EventMapper;
import com.tesi.federazione.backend.model.Enrollment;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.CompetitionType;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.repository.EnrollmentRepository;
import com.tesi.federazione.backend.repository.EventRepository;
import com.tesi.federazione.backend.security.SecurityUtils;
import com.tesi.federazione.backend.service.ClubService;
import com.tesi.federazione.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private EnrollmentMapper enrollmentMapper;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private ClubService clubService;
    @Mock
    private UserService userService;

    @InjectMocks
    private EventServiceImpl eventService;

    @Nested
    @DisplayName("Tests per: getAllEvents()")
    class GetAllEventsTest {
        @Test
        @DisplayName("SUCCESSO: ci sono eventi e la lista viene restituita")
        void success_eventList() {
            Event event = new Event();
            event.setId("1234");

            EventDTO eventDTO = new EventDTO();
            eventDTO.setId(event.getId());
            when(eventMapper.toDTO(event)).thenReturn(eventDTO);

            when(eventRepository.findAll()).thenReturn(List.of(event));

            List<EventDTO> result = eventService.getAllEvents();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(eventRepository).findAll();
            verify(eventMapper).toDTO(event);
        }

        @Test
        @DisplayName("SUCCESSO: non ci sono eventi e restituisce la lista vuota")
        void success_emptyList() {
            when(eventRepository.findAll()).thenReturn(List.of());

            List<EventDTO> result = eventService.getAllEvents();

            assertNotNull(result);
            assertEquals(0, result.size());
            verify(eventRepository).findAll();
            verify(eventMapper, never()).toDTO(any());
        }
    }

    @Nested
    @DisplayName("Tests per: getEventById()")
    class GetEventByIdTest {
        @Test
        @DisplayName("SUCCESSO: evento trovato e aggiunto conteggio")
        void success() {
            String eventId = "id";
            Event event = new Event();
            event.setId(eventId);

            EventDTO eventDTO = new EventDTO();
            eventDTO.setId(event.getId());
            when(eventMapper.toDTO(event)).thenReturn(eventDTO);

            List<Enrollment> enrollments = List.of(new Enrollment(), new Enrollment());
            when(enrollmentRepository.findByEventIdAndStatus(eventId, EnrollmentStatus.APPROVED)).thenReturn(enrollments);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            EventDTO result = eventService.getEventById(eventId);

            assertNotNull(result);
            assertEquals(2, result.getEnrolledCount());
            verify(eventRepository).findById(eventId);
            verify(enrollmentRepository).findByEventIdAndStatus(eventId, EnrollmentStatus.APPROVED);
        }

        @Test
        @DisplayName("FALLIMENTO: evento non trovato")
        void fail() {
            String eventId = "id";
            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(eventId));
            verify(enrollmentRepository, never()).findByEventId(anyString());
        }
    }


    @Nested
    @DisplayName("Tests per: createEvent()")
    class CreateEventTest {
        @Test
        @DisplayName("SUCCESSO: evento creato")
        void success() {
            CreateEventDTO dto = new CreateEventDTO();
            dto.setName("Nuovo evento");

            Event event = new Event();
            event.setId("id");
            event.setName("Nuovo evento");

            when(eventRepository.save(any(Event.class))).thenReturn(event);

            EventDTO eventDTO = new EventDTO();
            eventDTO.setId("id");
            eventDTO.setName("Nuovo evento");
            when(eventMapper.toDTO(event)).thenReturn(eventDTO);

            EventDTO result = eventService.createEvent(dto);

            assertNotNull(result);
            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            verify(eventRepository).save(eventCaptor.capture());
            Event savedEvent = eventCaptor.getValue();

            assertEquals(EventStatus.SCHEDULED, savedEvent.getStatus());
            assertEquals(dto.getName(), savedEvent.getName());
            verify(eventMapper).toDTO(any());
        }
    }

    @Nested
    @DisplayName("Tests per: updateEvent()")
    class UpdateEventTest {

        @Test
        @DisplayName("SUCCESSO: Aggiorna i dati e preserva lo stato originale")
        void success() {
            String eventId = "eventId";

            EventDTO eventDTO = new EventDTO();
            eventDTO.setId(eventId);
            eventDTO.setName("Nome Aggiornato");
            eventDTO.setDescription("Nuova descrizione");

            Event oldEvent = new Event();
            oldEvent.setId(eventId);
            oldEvent.setName("Vecchio Nome");
            oldEvent.setStatus(EventStatus.REGISTRATION_OPEN);

            Event savedEvent = new Event();
            savedEvent.setId(eventId);
            savedEvent.setName("Nome Aggiornato");
            savedEvent.setDescription("Nuova descrizione");
            savedEvent.setStatus(EventStatus.REGISTRATION_OPEN);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(oldEvent));
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
            when(eventMapper.toDTO(savedEvent)).thenReturn(new EventDTO());


            EventDTO result = eventService.updateEvent(eventDTO);

            assertNotNull(result);
            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            verify(eventRepository).save(eventCaptor.capture());
            Event capturedEvent = eventCaptor.getValue();

            assertEquals("Nome Aggiornato", capturedEvent.getName());
            assertEquals("Nuova descrizione", capturedEvent.getDescription());
            assertEquals(EventStatus.REGISTRATION_OPEN, capturedEvent.getStatus());
            assertEquals(eventId, capturedEvent.getId());
        }

        @Test
        @DisplayName("FALLIMENTO: Evento non trovato")
        void fail_NotFound() {
            EventDTO dto = new EventDTO();
            dto.setId("id");

            when(eventRepository.findById("id")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                eventService.updateEvent(dto);
            });

            verify(eventRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests per: updateEventState()")
    class UpdateEventStateTest {

        @Test
        @DisplayName("FALLIMENTO: Evento non trovato")
        void fail_NotFound() {
            EventDTO dto = new EventDTO();
            dto.setId("id");

            when(eventRepository.findById("id")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                eventService.updateEvent(dto);
            });

            verify(eventRepository, never()).save(any());
        }


        @Test
        @DisplayName("FALLIMENTO: Stato non gestitio")
        void fail_InvalidState() {
            String eventId = "eventId";
            EventStatus newStatus = null;

            Event event = new Event();
            event.setId(eventId);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            assertThrows(ActionNotAllowedException.class, () -> {
                eventService.updateEventState(eventId, newStatus);
            });

            verify(eventRepository, never()).save(any());
        }

        @Test
        @DisplayName("SUCCESSO: Transizione da SCHEDULED a REGISTRATION_OPEN")
        void success_OpenRegistrations() {
            String eventId = "eventId";
            EventStatus targetStatus = EventStatus.REGISTRATION_OPEN;

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.SCHEDULED);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            eventService.updateEventState(eventId, targetStatus);

            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            verify(eventRepository).save(eventCaptor.capture());

            Event savedEvent = eventCaptor.getValue();

            assertEquals(EventStatus.REGISTRATION_OPEN, savedEvent.getStatus());
        }

        @Test
        @DisplayName("SUCCESSO: Transizione da REGISTRATION_OPEN a REGISTRATION_CLOSED")
        void success_CloseRegistrations() {
            String eventId = "eventId";
            EventStatus targetStatus = EventStatus.REGISTRATION_CLOSED;

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.REGISTRATION_OPEN);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            eventService.updateEventState(eventId, targetStatus);

            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            verify(eventRepository).save(eventCaptor.capture());
            assertEquals(EventStatus.REGISTRATION_CLOSED, eventCaptor.getValue().getStatus());
        }

        @Test
        @DisplayName("SUCCESSO: Transizione da REGISTRATION_CLOSED a COMPLETED")
        void success_CompleteEvent() {
            String eventId = "eventId";

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.REGISTRATION_CLOSED);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            eventService.updateEventState(eventId, EventStatus.COMPLETED);

            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            verify(eventRepository).save(eventCaptor.capture());
            assertEquals(EventStatus.COMPLETED, eventCaptor.getValue().getStatus());
        }

        @Test
        @DisplayName("SUCCESSO: Transizione da SCHEDULED a CANCELLED")
        void success_CancelEvent() {
            String eventId = "eventId";

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.SCHEDULED);

            Enrollment enrollmentSubmitted = new Enrollment();
            enrollmentSubmitted.setId("enrollId1");
            enrollmentSubmitted.setStatus(EnrollmentStatus.SUBMITTED);

            Enrollment enrollmentDraft = new Enrollment();
            enrollmentDraft.setId("enrollId2");
            enrollmentDraft.setStatus(EnrollmentStatus.DRAFT);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(enrollmentRepository.findByEventId(eventId)).thenReturn(List.of(enrollmentSubmitted, enrollmentDraft));

            eventService.updateEventState(eventId, EventStatus.CANCELLED);

            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            verify(eventRepository).save(eventCaptor.capture());
            assertEquals(EventStatus.CANCELLED, eventCaptor.getValue().getStatus());

            ArgumentCaptor<List<Enrollment>> enrollmentListCaptor = ArgumentCaptor.forClass(List.class);
            verify(enrollmentRepository).saveAll(enrollmentListCaptor.capture());

            List<Enrollment> savedEnrollments = enrollmentListCaptor.getValue();

            // Mi aspetto che venga salvata solo quella NON draft
            assertEquals(1, savedEnrollments.size());
            assertEquals("enrollId1", savedEnrollments.get(0).getId());
            assertEquals(EnrollmentStatus.REJECTED, savedEnrollments.get(0).getStatus());
        }


        @Test
        @DisplayName("SUCCESSO: Transizione da CANCELLED a SCHEDULED")
        void success_ResumeEvent() {
            String eventId = "eventId";

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.CANCELLED);
            event.setDate(LocalDate.of(2030, 1, 1));

            Enrollment enrollmentRejected = new Enrollment();
            enrollmentRejected.setId("enrollId");
            enrollmentRejected.setStatus(EnrollmentStatus.REJECTED);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(enrollmentRepository.findByEventId(eventId)).thenReturn(List.of(enrollmentRejected));

            eventService.updateEventState(eventId, EventStatus.SCHEDULED);

            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            verify(eventRepository).save(eventCaptor.capture());
            assertEquals(EventStatus.SCHEDULED, eventCaptor.getValue().getStatus());

            ArgumentCaptor<List<Enrollment>> enrollmentListCaptor = ArgumentCaptor.forClass(List.class);
            verify(enrollmentRepository).saveAll(enrollmentListCaptor.capture());

            List<Enrollment> savedEnrollments = enrollmentListCaptor.getValue();

            // Mi aspetto che venga salvata solo quella NON draft
            assertEquals(1, savedEnrollments.size());
            assertEquals("enrollId", savedEnrollments.get(0).getId());
            assertEquals(EnrollmentStatus.DRAFT, savedEnrollments.get(0).getStatus());
        }

        @Test
        @DisplayName("FALLIMENTO: Transizione Illegale")
        void fail_IllegalTransition_LogicCheck() {
            String eventId = "eventId";

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.COMPLETED);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            assertThrows(ActionNotAllowedException.class, () -> {
                eventService.updateEventState(eventId, EventStatus.REGISTRATION_OPEN);
            });

            verify(eventRepository, never()).save(any());
        }

    }

    @Nested
    @DisplayName("Tests per: enrollAthlete()")
    class EnrollAthleteTest {
        @Test
        @DisplayName("SUCCESSO: Club Manager iscrive un suo atleta ")
        void success_ManagerSubmitted() {
            CreateEnrollmentDTO dto = new CreateEnrollmentDTO();
            dto.setEventId("eventId");
            dto.setClubId("clubId");
            dto.setAthleteId("userId");
            dto.setCompetitionType(CompetitionType.BOXE);
            dto.setDraft(false);

            Event mockEvent = new Event();
            mockEvent.setId("eventId");
            mockEvent.setStatus(EventStatus.REGISTRATION_OPEN);
            ClubDTO clubDTO = new ClubDTO();
            clubDTO.setName("Clubname");

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub("clubId")).thenReturn(true);
            when(enrollmentRepository.existsByEventIdAndAthleteIdAndDiscipline(any(), any(), any())).thenReturn(false);
            when(clubService.getClubById(any())).thenReturn(clubDTO);

            when(eventRepository.findById("eventId")).thenReturn(Optional.of(mockEvent));
            when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(new Enrollment());
            when(enrollmentMapper.toDTO(any())).thenReturn(new EnrollmentDTO());

            eventService.enrollAthlete(dto);

            ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
            verify(enrollmentRepository).save(captor.capture());
            Enrollment saved = captor.getValue();

            assertEquals(EnrollmentStatus.SUBMITTED, saved.getStatus());
            assertEquals("userId", saved.getAthleteId());
            assertNotNull(saved.getEnrollmentDate());
        }

        @Test
        @DisplayName("SUCCESSO: Atleta crea bozza per se stesso")
        void success_athleteDraft() {
            CreateEnrollmentDTO dto = new CreateEnrollmentDTO();
            dto.setEventId("eventId");
            dto.setAthleteId("me");
            dto.setDraft(true);

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn("me");

            Event mockEvent = new Event();
            mockEvent.setId("eventId");
            mockEvent.setStatus(EventStatus.REGISTRATION_OPEN);

            ClubDTO clubDTO = new ClubDTO();
            clubDTO.setName("Clubname");

            when(clubService.getClubById(any())).thenReturn(clubDTO);
            when(eventRepository.findById("eventId")).thenReturn(Optional.of(mockEvent));
            when(enrollmentRepository.save(any())).thenReturn(new Enrollment());
            when(enrollmentMapper.toDTO(any())).thenReturn(new EnrollmentDTO());

            eventService.enrollAthlete(dto);

            ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
            verify(enrollmentRepository).save(captor.capture());
            assertEquals(EnrollmentStatus.DRAFT, captor.getValue().getStatus());
        }

        @Test
        @DisplayName("FALLIMENTO: Club Manager iscrive un atleta di altro club")
        void fail_managerUnauthorized() {
            CreateEnrollmentDTO dto = new CreateEnrollmentDTO();
            dto.setEventId("eventId");
            dto.setClubId("clubId");
            dto.setAthleteId("userId");
            dto.setCompetitionType(CompetitionType.BOXE);
            dto.setDraft(false);

            Event mockEvent = new Event();
            mockEvent.setId("eventId");
            mockEvent.setStatus(EventStatus.REGISTRATION_OPEN);

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub("clubId")).thenReturn(false);

            assertThrows(UnauthorizedException.class, () -> eventService.enrollAthlete(dto));
        }

        @Test
        @DisplayName("FALLIMENTO: Atleta tenta di iscrivere un altro")
        void fail_athleteOther() {
            CreateEnrollmentDTO dto = new CreateEnrollmentDTO();
            dto.setAthleteId("123");

            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn("me");

            assertThrows(UnauthorizedException.class, () -> eventService.enrollAthlete(dto));
        }

        @Test
        @DisplayName("FALLIMENTO: Atleta tenta di inviare SUBMITTED")
        void fail_athleteSubmit() {
            CreateEnrollmentDTO dto = new CreateEnrollmentDTO();
            dto.setAthleteId("me");
            dto.setDraft(false);

            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn("me");

            assertThrows(UnauthorizedException.class, () -> eventService.enrollAthlete(dto));
        }

        @Test
        @DisplayName("FALLIMENTO: Iscrizione Duplicata")
        void fail_resourceConflict() {
            CreateEnrollmentDTO dto = new CreateEnrollmentDTO();
            dto.setEventId("eventId");
            dto.setAthleteId("123");
            dto.setCompetitionType(CompetitionType.BOXE);

            when(enrollmentRepository.existsByEventIdAndAthleteIdAndDiscipline("eventId", "123", CompetitionType.BOXE))
                    .thenReturn(true);

            assertThrows(ResourceConflictException.class, () -> eventService.enrollAthlete(dto));
        }
    }

    @Nested
    @DisplayName("Tests per: getEnrollmentsByEventId()")
    class GetEnrollmentsByEventIdTest {

        @Test
        @DisplayName("SUCCESSO: Federation Manager vede tutto")
        void success_FederationManager() {
            String eventId = "eventId";
            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(false);

            when(enrollmentRepository.findByEventId(eventId)).thenReturn(Collections.emptyList());

            eventService.getEnrollmentsByEventId(eventId, null, null);

            verify(enrollmentRepository).findByEventId(eventId);
        }

        @Test
        @DisplayName("SUCCESSO: Club Manager vede solo il suo club ")
        void success_clubManager() {
            String eventId = "eventId";
            String myClub = "my-club";

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getUserClub()).thenReturn(myClub);

            when(enrollmentRepository.findByEventIdAndClubId(eventId, myClub)).thenReturn(Collections.emptyList());

            eventService.getEnrollmentsByEventId(eventId, null, null);

            verify(enrollmentRepository).findByEventIdAndClubId(eventId, myClub);
        }


        @Test
        @DisplayName("SUCCESSO: atleta vede solo le sue iscrizioni")
        void success_athlete() {
            String eventId = "eventId";
            String athleteId = "me";

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn("me");

            when(enrollmentRepository.findByEventIdAndAthleteId(eventId, athleteId)).thenReturn(Collections.emptyList());

            eventService.getEnrollmentsByEventId(eventId, null, null);

            verify(enrollmentRepository).findByEventIdAndAthleteId(eventId, athleteId);
        }

        @Test
        @DisplayName("FALLIMENTO: Club Manager chiede dati di altro club")
        void fail_clubManagerUnauthorized() {
            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getUserClub()).thenReturn("1234");

            assertThrows(UnauthorizedException.class, () ->
                    eventService.getEnrollmentsByEventId("eventId", "other-club", null)
            );
        }

        @Test
        @DisplayName("FALLIMENTO: Club Manager chiede dati di altro club")
        void fail_athleteUnauthorized() {
            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn("1234");

            assertThrows(UnauthorizedException.class, () ->
                    eventService.getEnrollmentsByEventId("eventId", null, "otherId")
            );
        }
    }

    @Test
    @DisplayName("Test per: getApprovedEnrollmentsByEventId")
    void success_getApprovedEnrollmentsByEventId() {
        String eventId = "eventId";
        when(enrollmentRepository.findByEventIdAndStatus(eventId, EnrollmentStatus.APPROVED)).thenReturn(Collections.emptyList());

        eventService.getApprovedEnrollmentsByEventId(eventId);

        verify(enrollmentRepository).findByEventIdAndStatus(eventId, EnrollmentStatus.APPROVED);
    }

    @Nested
    @DisplayName("Tests per: getEnrollment()")
    class GetEnrollmentTest {

        @Test
        @DisplayName("SUCCESSO: Federation Manager richiede un'iscrizione")
        void success_FederationManager() {
            String enrollId = "enrollId";
            Enrollment enrollment = new Enrollment();
            enrollment.setId(enrollId);

            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setId(enrollId);

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(false);

            when(enrollmentRepository.findById(enrollId)).thenReturn(Optional.of(enrollment));
            when(enrollmentMapper.toDTO(enrollment)).thenReturn(enrollmentDTO);

            eventService.getEnrollment(enrollId);

            verify(enrollmentRepository).findById(enrollId);
            verify(enrollmentMapper).toDTO(enrollment);
        }

        @Test
        @DisplayName("FALLIMENTO: Club Manager richiede un'iscrizione di altro club")
        void fail_ClubManager_otherClubEnrollment() {
            String enrollId = "enrollId";

            Enrollment enrollment = new Enrollment();
            enrollment.setId(enrollId);
            enrollment.setClubId("other-club");


            when(enrollmentRepository.findById(enrollId)).thenReturn(Optional.of(enrollment));
            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub(enrollment.getClubId())).thenReturn(false);

            assertThrows(UnauthorizedException.class, () -> eventService.getEnrollment(enrollId));
            verify(enrollmentMapper, never()).toDTO(enrollment);
        }


        @Test
        @DisplayName("FALLIMENTO: Atleta richiede un'iscrizione di altro atleta")
        void fail_athlete_otherAthleteEnrollment() {
            String enrollId = "enrollId";

            Enrollment enrollment = new Enrollment();
            enrollment.setId(enrollId);
            enrollment.setAthleteId("other");

            when(enrollmentRepository.findById(enrollId)).thenReturn(Optional.of(enrollment));
            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn("id");

            assertThrows(UnauthorizedException.class, () -> eventService.getEnrollment(enrollId));
            verify(enrollmentMapper, never()).toDTO(enrollment);
        }
    }

    @Nested
    @DisplayName("Tests per: updateEnrollment()")
    class UpdateEnrollmentTest {
        @Test
        @DisplayName("FALLIMENTO: Club Manager aggiorna iscrizione di atleti di altro club")
        void fail_clubManagerUnauthorized() {
            String clubId = "clubId";
            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setClubId(clubId);

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub(enrollmentDTO.getClubId())).thenReturn(false);

            assertThrows(UnauthorizedException.class, () -> eventService.updateEnrollment(enrollmentDTO));
        }

        @Test
        @DisplayName("FALLIMENTO: Atleta aggiorna iscrizione di altro atleta")
        void fail_athleteUnauthorized() {
            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setClubId("clubId");
            enrollmentDTO.setAthleteId("other");

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn("me");

            assertThrows(UnauthorizedException.class, () -> eventService.updateEnrollment(enrollmentDTO));
        }


        @Test
        @DisplayName("FALLIMENTO: Atleta aggiorna iscrizione con stato non in bozza")
        void fail_athleteUnauthorized2() {
            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setClubId("clubId");
            enrollmentDTO.setAthleteId("me");
            enrollmentDTO.setStatus(EnrollmentStatus.SUBMITTED);

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn("me");

            assertThrows(UnauthorizedException.class, () -> eventService.updateEnrollment(enrollmentDTO));
        }

        @Test
        @DisplayName("FALLIMENTO: iscrizione non trovata")
        void fail_enrollmentNotFound() {
            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setClubId("clubId");
            enrollmentDTO.setAthleteId("me");
            enrollmentDTO.setStatus(EnrollmentStatus.SUBMITTED);

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(false);
            when(enrollmentRepository.findById(enrollmentDTO.getId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> eventService.updateEnrollment(enrollmentDTO));
        }


        @Test
        @DisplayName("FALLIMENTO: evento indicato non trovato")
        void fail_eventNotFound() {
            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setClubId("clubId");
            enrollmentDTO.setAthleteId("me");
            enrollmentDTO.setEventId("eventId");
            enrollmentDTO.setStatus(EnrollmentStatus.SUBMITTED);

            Enrollment enrollment = new Enrollment();

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(false);
            when(enrollmentRepository.findById(enrollmentDTO.getId())).thenReturn(Optional.of(enrollment));
            when(eventRepository.findById(enrollmentDTO.getEventId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> eventService.updateEnrollment(enrollmentDTO));
        }


        @Test
        @DisplayName("FALLIMENTO: registrazione non valida")
        void fail_invalidRegistration() {
            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setClubId("clubId");
            enrollmentDTO.setAthleteId("me");
            enrollmentDTO.setEventId("eventId");
            enrollmentDTO.setStatus(EnrollmentStatus.SUBMITTED);

            Enrollment enrollment = new Enrollment();
            enrollment.setClubId("clubId");
            enrollment.setAthleteId("me");
            enrollment.setEventId("eventId");
            enrollment.setStatus(EnrollmentStatus.DRAFT);

            Event event = new Event();
            event.setId(enrollmentDTO.getEventId());
            event.setStatus(EventStatus.SCHEDULED);

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(false);
            when(securityUtils.isFederationManager()).thenReturn(true);
            when(enrollmentRepository.findById(enrollmentDTO.getId())).thenReturn(Optional.of(enrollment));
            when(eventRepository.findById(enrollmentDTO.getEventId())).thenReturn(Optional.of(event));

            assertThrows(ActionNotAllowedException.class, () -> eventService.updateEnrollment(enrollmentDTO));
        }

        @Test
        @DisplayName("FALLIMENTO: iscrizione duplicata")
        void fail_conflictResource() {
            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setClubId("clubId");
            enrollmentDTO.setAthleteId("me");
            enrollmentDTO.setEventId("eventId");
            enrollmentDTO.setStatus(EnrollmentStatus.SUBMITTED);
            enrollmentDTO.setCompetitionType(CompetitionType.BOXE);

            Enrollment enrollment = new Enrollment();
            enrollment.setClubId("clubId");
            enrollment.setAthleteId("me");
            enrollment.setEventId("eventId");
            enrollment.setStatus(EnrollmentStatus.DRAFT);
            enrollment.setDiscipline(CompetitionType.KICK_BOXING);

            Event event = new Event();
            event.setId(enrollmentDTO.getEventId());
            event.setStatus(EventStatus.REGISTRATION_OPEN);

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(false);
            when(securityUtils.isFederationManager()).thenReturn(true);
            when(enrollmentRepository.findById(enrollmentDTO.getId())).thenReturn(Optional.of(enrollment));
            when(eventRepository.findById(enrollmentDTO.getEventId())).thenReturn(Optional.of(event));
            when(enrollmentRepository.existsByEventIdAndAthleteIdAndDiscipline("eventId", "me", CompetitionType.BOXE)).thenReturn(true);

            assertThrows(ResourceConflictException.class, () -> eventService.updateEnrollment(enrollmentDTO));
        }

        @Test
        @DisplayName("SUCCESSO: iscrizione modificata correttamente")
        void success() {
            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setId("id");
            enrollmentDTO.setClubId("clubId");
            enrollmentDTO.setAthleteId("me");
            enrollmentDTO.setEventId("eventId");
            enrollmentDTO.setStatus(EnrollmentStatus.SUBMITTED);
            enrollmentDTO.setCompetitionType(CompetitionType.BOXE);

            Enrollment enrollment = new Enrollment();
            enrollment.setId("id");
            enrollment.setClubId("clubId");
            enrollment.setAthleteId("me");
            enrollment.setEventId("eventId");
            enrollment.setStatus(EnrollmentStatus.DRAFT);
            enrollment.setDiscipline(CompetitionType.KICK_BOXING);

            Event event = new Event();
            event.setId(enrollmentDTO.getEventId());
            event.setStatus(EventStatus.REGISTRATION_OPEN);

            Enrollment updatedEnrollment = new Enrollment();
            updatedEnrollment.setClubId("clubId");
            updatedEnrollment.setAthleteId("me");
            updatedEnrollment.setEventId("eventId");
            updatedEnrollment.setStatus(EnrollmentStatus.SUBMITTED);
            updatedEnrollment.setDiscipline(CompetitionType.BOXE);

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(false);
            when(securityUtils.isFederationManager()).thenReturn(true);
            when(enrollmentRepository.findById(enrollmentDTO.getId())).thenReturn(Optional.of(enrollment));
            when(eventRepository.findById(enrollmentDTO.getEventId())).thenReturn(Optional.of(event));
            when(enrollmentRepository.existsByEventIdAndAthleteIdAndDiscipline("eventId", "me", CompetitionType.BOXE)).thenReturn(false);

            when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(updatedEnrollment);
            when(enrollmentMapper.toDTO(any(Enrollment.class))).thenReturn(enrollmentDTO);

            EnrollmentDTO result = eventService.updateEnrollment(enrollmentDTO);

            assertNotNull(result);
            ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
            verify(enrollmentRepository).save(captor.capture());
            Enrollment savedEnrollment = captor.getValue();

            assertEquals(CompetitionType.BOXE, savedEnrollment.getDiscipline());
            assertEquals(EnrollmentStatus.SUBMITTED, savedEnrollment.getStatus());
            verify(enrollmentRepository).existsByEventIdAndAthleteIdAndDiscipline("eventId", "me", CompetitionType.BOXE);
            verify(enrollmentMapper).toDTO(any(Enrollment.class));
        }

    }

    @Nested
    @DisplayName("Tests per: updateEnrollmentStatus()")
    class UpdateEnrollmentStatusTest {

        @Test
        @DisplayName("FALLIMENTO: iscrizione non trovata")
        void fail_enrollmentNotFound() {
            String enrollId = "enrollId";
            EnrollmentStatus newStatus = EnrollmentStatus.DRAFT;

            when(enrollmentRepository.findById(enrollId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> eventService.updateEnrollmentStatus(enrollId, newStatus));
        }

        @Test
        @DisplayName("FALLIMENTO: Club Manager aggiorna iscrizione di atleti di altro club")
        void fail_clubManagerUnauthorized() {
            String enrollId = "enrollId";
            String eventId = "eventId";
            EnrollmentStatus newStatus = EnrollmentStatus.DRAFT;

            Enrollment enrollment = new Enrollment();
            enrollment.setId(enrollId);
            enrollment.setEventId(eventId);
            enrollment.setClubId("clubId");

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.REGISTRATION_OPEN);

            when(enrollmentRepository.findById(enrollId)).thenReturn(Optional.of(enrollment));
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub(enrollment.getClubId())).thenReturn(false);

            assertThrows(UnauthorizedException.class, () -> eventService.updateEnrollmentStatus(enrollId, newStatus));
        }

        @Test
        @DisplayName("FALLIMENTO: Club Manager tenta di approvare")
        void fail_ClubManagerCannotApprove() {
            String enrollId = "enrollId";
            String eventId = "eventId";

            Enrollment enrollment = new Enrollment();
            enrollment.setId(enrollId);
            enrollment.setEventId(eventId);
            enrollment.setClubId("myClub");
            enrollment.setStatus(EnrollmentStatus.SUBMITTED);

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.REGISTRATION_OPEN);

            when(enrollmentRepository.findById(enrollId)).thenReturn(Optional.of(enrollment));
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub("myClub")).thenReturn(true);

            assertThrows(ActionNotAllowedException.class, () ->
                    eventService.updateEnrollmentStatus(enrollId, EnrollmentStatus.APPROVED)
            );

            verify(enrollmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("FALLIMENTO: atleta prova a sottomettere")
        void fail_AthleteCannotSubmit() {
            String enrollId = "enrollId";
            String eventId = "eventId";

            Enrollment enrollment = new Enrollment();
            enrollment.setId(enrollId);
            enrollment.setEventId(eventId);
            enrollment.setClubId("myClub");
            enrollment.setStatus(EnrollmentStatus.SUBMITTED);

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.REGISTRATION_OPEN);

            when(enrollmentRepository.findById(enrollId)).thenReturn(Optional.of(enrollment));
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(true);

            assertThrows(ActionNotAllowedException.class, () ->
                    eventService.updateEnrollmentStatus(enrollId, EnrollmentStatus.APPROVED)
            );

            verify(enrollmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("FALLIMENTO: cambio di stato non concesso")
        void fail_invalidTransition() {
            String enrollId = "enrollId";
            String eventId = "eventId";
            EnrollmentStatus oldStatus = EnrollmentStatus.APPROVED;
            EnrollmentStatus newStatus = EnrollmentStatus.REJECTED;

            Enrollment enrollment = new Enrollment();
            enrollment.setId(enrollId);
            enrollment.setEventId(eventId);
            enrollment.setStatus(oldStatus);

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.REGISTRATION_OPEN);

            when(enrollmentRepository.findById(enrollId)).thenReturn(Optional.of(enrollment));
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            assertThrows(ActionNotAllowedException.class, () -> eventService.updateEnrollmentStatus(enrollId, newStatus));

            verify(enrollmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("SUCCESSO: federation manager approva cambio stato")
        void success() {
            String enrollId = "enrollId";
            String eventId = "eventId";
            EnrollmentStatus oldStatus = EnrollmentStatus.SUBMITTED;
            EnrollmentStatus newStatus = EnrollmentStatus.APPROVED;

            Enrollment enrollment = new Enrollment();
            enrollment.setId(enrollId);
            enrollment.setEventId(eventId);
            enrollment.setStatus(oldStatus);

            Event event = new Event();
            event.setId(eventId);
            event.setStatus(EventStatus.REGISTRATION_OPEN);

            when(enrollmentRepository.findById(enrollId)).thenReturn(Optional.of(enrollment));
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(false);
            when(securityUtils.isFederationManager()).thenReturn(true);

            when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
            when(enrollmentMapper.toDTO(any(Enrollment.class))).thenReturn(new EnrollmentDTO());

            eventService.updateEnrollmentStatus(enrollId, newStatus);

            ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
            verify(enrollmentRepository).save(captor.capture());
            assertEquals(EnrollmentStatus.APPROVED, captor.getValue().getStatus());
        }

    }
}
