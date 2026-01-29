package com.tesi.federazione.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.security.JwtUtils;
import com.tesi.federazione.backend.service.AthleteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AthleteController.class)
@AutoConfigureMockMvc(addFilters = false)
class AthleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AthleteService athleteService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test per GET /to-approve/{clubId}")
    void getAthletesToApproveTest() throws Exception {
        String clubId = "clubId";
        AthleteDTO athlete1 = new AthleteDTO();
        athlete1.setId("userId");
        athlete1.setFirstName("Mario");
        athlete1.setAffiliationStatus(AffiliationStatus.SUBMITTED);

        when(athleteService.getAthletesByStatusAndClubId(AffiliationStatus.SUBMITTED, clubId))
                .thenReturn(List.of(athlete1));

        mockMvc.perform(get("/api/athlete/to-approve/{id}", clubId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value("userId"))
                .andExpect(jsonPath("$[0].affiliationStatus").value("SUBMITTED"));
    }

    @Test
    @DisplayName("Test per POST /update-status/{id}/{newStatus}")
    void updateAthleteStatusTest() throws Exception {
        String athleteId = "userId";
        AffiliationStatus newStatus = AffiliationStatus.ACCEPTED;

        doNothing().when(athleteService).updateStatus(athleteId, newStatus);

        mockMvc.perform(post("/api/athlete/update-status/{id}/{newStatus}", athleteId, newStatus)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test per POST /renew-submission/{id}")
    void renewAthleteSubmissionStatusTest() throws Exception {
        String athleteId = "userId";

        doNothing().when(athleteService).updateStatus(athleteId, AffiliationStatus.SUBMITTED);

        mockMvc.perform(post("/api/athlete/renew-submission/{id}", athleteId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test per GET club/{clubId}")
    void getAthleteByClubId() throws Exception {
        String clubId = "clubId";
        AthleteDTO a1 = new AthleteDTO();
        a1.setId("1");
        AthleteDTO a2 = new AthleteDTO();
        a2.setId("2");

        when(athleteService.getAthletesByClubId(clubId)).thenReturn(List.of(a1,a2));

        mockMvc.perform(get("/api/athlete/club/{clubId}", clubId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    // --- GET /api/athlete/all ---
    @Test
    @DisplayName("Test per GET /all")
    void getAllAthletes() throws Exception {
        AthleteDTO a1 = new AthleteDTO();
        a1.setId("1");

        when(athleteService.getAllAthletes()).thenReturn(List.of(a1));

        mockMvc.perform(get("/api/athlete/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}