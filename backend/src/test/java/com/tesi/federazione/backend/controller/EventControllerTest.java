package com.tesi.federazione.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.security.JwtUtils;
import com.tesi.federazione.backend.service.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Event endpoint tests")
    class EventTests {

        @Test
        @DisplayName("Test per GET /all")
        void getAllEventsTest() throws Exception {
            EventDTO event = new EventDTO();
            event.setId("eventId");

            when(eventService.getAllEvents()).thenReturn(List.of(event));

            mockMvc.perform(get("/api/event/all")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].id").value("eventId"));
        }

        @Test
        @DisplayName("Test per GET /{id}")
        void getEventByIdTest() throws Exception {
            String eventId = "eventId";
            EventDTO event = new EventDTO();
            event.setId(eventId);

            when(eventService.getEventById(eventId)).thenReturn(event);

            mockMvc.perform(get("/api/event/{id}", eventId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(eventId));
        }

        @Test
        @DisplayName("Test per POST /create")
        void createNewEventTest() throws Exception {
            CreateEventDTO createDto = new CreateEventDTO();
            createDto.setName("Evento");

            EventDTO responseDto = new EventDTO();
            responseDto.setId("eventId");
            responseDto.setName("Evento");

            when(eventService.createEvent(any(CreateEventDTO.class))).thenReturn(responseDto);

            mockMvc.perform(post("/api/event/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("eventId"))
                    .andExpect(jsonPath("$.name").value("Evento"));
        }

        @Test
        @DisplayName("Test per PATCH /update")
        void updatedEventTest() throws Exception {
            EventDTO updateDto = new EventDTO();
            updateDto.setId("eventId");
            updateDto.setName("Nuovo Nome");

            when(eventService.updateEvent(any(EventDTO.class))).thenReturn(updateDto);

            mockMvc.perform(patch("/api/event/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Nuovo Nome"));
        }

        @Test
        @DisplayName("Test per PATCH /update-state/{id}")
        void updateEventStateTest() throws Exception {
            String eventId = "eventId";
            EventStatus newState = EventStatus.REGISTRATION_OPEN;

            doNothing().when(eventService).updateEventState(eventId, newState);

            mockMvc.perform(patch("/api/event/update-state/{id}", eventId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newState))
                    )
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Enrollment endpoint tests")
    class EnrollmentTests {

        @Test
        @DisplayName("Test per POST /enroll")
        void enrollAthleteTest() throws Exception {
            CreateEnrollmentDTO createEnrollmentDTO = new CreateEnrollmentDTO();
            createEnrollmentDTO.setAthleteId("userId");
            createEnrollmentDTO.setEventId("eventId");

            EnrollmentDTO responseDto = new EnrollmentDTO();
            responseDto.setId("enrollId");
            responseDto.setAthleteId("userId");

            when(eventService.enrollAthlete(createEnrollmentDTO)).thenReturn(responseDto);

            mockMvc.perform(post("/api/event/enroll")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createEnrollmentDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("enrollId"));
        }

        @Test
        @DisplayName("Test per GET /enroll/{id}")
        void getEnrollmentTest() throws Exception {
            String enrollId = "enrollId";
            EnrollmentDTO dto = new EnrollmentDTO();
            dto.setId(enrollId);

            when(eventService.getEnrollment(enrollId)).thenReturn(dto);

            mockMvc.perform(get("/api/event/enroll/{id}", enrollId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(enrollId));
        }

        @Test
        @DisplayName("Test per GET /enroll-all/{eventId}")
        void getEnrollmentsByEventIdTest() throws Exception {
            String eventId = "eventId";

            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setId("enrollId");

            when(eventService.getEnrollmentsByEventId(eq(eventId), eq(null), eq(null)))
                    .thenReturn(List.of(enrollmentDTO));

            mockMvc.perform(get("/api/event/enroll-all/{eventId}", eventId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1));
        }

        @Test
        @DisplayName("Test per GET /enroll-approved-all/{eventId}")
        void getApprovedEnrollmentsByEventIdTest() throws Exception {
            String eventId = "eventId";

            EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
            enrollmentDTO.setId("enrollId");
            enrollmentDTO.setStatus(EnrollmentStatus.APPROVED);

            when(eventService.getApprovedEnrollmentsByEventId(eq(eventId)))
                    .thenReturn(List.of(enrollmentDTO));

            mockMvc.perform(get("/api/event/enroll-approved-all/{eventId}", eventId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1));
        }

        @Test
        @DisplayName("Test per PATCH /enroll/update")
        void updatedEnrollmentTest() throws Exception {
            EnrollmentDTO updateDto = new EnrollmentDTO();
            updateDto.setId("enrollId");

            when(eventService.updateEnrollment(any(EnrollmentDTO.class))).thenReturn(updateDto);

            mockMvc.perform(patch("/api/event/enroll/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("enrollId"));
        }

        @Test
        @DisplayName("Test per PATCH /enroll/update-status/{id}")
        void updatedEnrollmentStatusTest() throws Exception {
            String enrollId = "enrollId";
            EnrollmentStatus newStatus = EnrollmentStatus.APPROVED;

            EnrollmentDTO responseDto = new EnrollmentDTO();
            responseDto.setId(enrollId);
            responseDto.setStatus(newStatus);

            when(eventService.updateEnrollmentStatus(enrollId, newStatus)).thenReturn(responseDto);

            mockMvc.perform(patch("/api/event/enroll/update-status/{enrollId}", enrollId)
                            .content(objectMapper.writeValueAsString(newStatus))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("APPROVED"));
        }
    }
}