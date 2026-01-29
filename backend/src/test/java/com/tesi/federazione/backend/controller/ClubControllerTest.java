package com.tesi.federazione.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.security.JwtUtils;
import com.tesi.federazione.backend.service.ClubService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClubController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClubService clubService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test per: POST /create")
    void createClubTest() throws Exception {
        CreateClubDTO createClubDto = new CreateClubDTO();
        createClubDto.setName("club");
        createClubDto.setFiscalCode("1234567");

        ClubDTO responseDto = new ClubDTO();
        responseDto.setId("clubId");
        responseDto.setName("club");

        when(clubService.createClub(createClubDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/club/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClubDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("clubId"))
                .andExpect(jsonPath("$.name").value("club"));
    }

    @Test
    @DisplayName("Test per: PATCH /update/{id}")
    void updateClubTest() throws Exception {
        String clubId = "clubId";
        UpdatedClubDTO updatedClubDTO = new UpdatedClubDTO();
        updatedClubDTO.setId(clubId);
        updatedClubDTO.setName("Club");

        ClubDTO responseDto = new ClubDTO();
        responseDto.setId(clubId);
        responseDto.setName("Club");

        when(clubService.updateClub(updatedClubDTO)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/club/update/{id}", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClubDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Club"));
    }

    @Test
    @DisplayName("Test per GET /{id}")
    void getClubByIdTest() throws Exception {
        String clubId = "clubId";
        ClubDTO clubDto = new ClubDTO();
        clubDto.setId(clubId);
        clubDto.setName("Test Club");

        when(clubService.getClubById(clubId)).thenReturn(clubDto);

        mockMvc.perform(get("/api/club/{id}", clubId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clubId))
                .andExpect(jsonPath("$.name").value("Test Club"));
    }

    @Test
    @DisplayName("Test per GET /to-approve")
    void getClubsToApproveTest() throws Exception {
        ClubDTO club = new ClubDTO();
        club.setId("1");
        club.setAffiliationStatus(AffiliationStatus.SUBMITTED);

        when(clubService.getClubsByStatus(AffiliationStatus.SUBMITTED)).thenReturn(List.of(club));

        mockMvc.perform(get("/api/club/to-approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].affiliationStatus").value("SUBMITTED"));
    }

    @Test
    @DisplayName("Test per GET /all")
    void getAllClubsTest() throws Exception {
        ClubDTO club1 = new ClubDTO(); club1.setId("clubId1");
        ClubDTO club2 = new ClubDTO(); club2.setId("clubId2");

        when(clubService.getAll()).thenReturn(List.of(club1, club2));

        mockMvc.perform(get("/api/club/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    @DisplayName("Test per POST /renew-submission/{id}")
    void renewClubAffiliationStatusTest() throws Exception {
        String clubId = "clubId";

        doNothing().when(clubService).updateClubStatus(clubId, AffiliationStatus.SUBMITTED);

        mockMvc.perform(post("/api/club/renew-submission/{id}", clubId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test per POST /update-status/{id}/{newStatus}")
    void updateAffiliationStatusTest() throws Exception {
        String clubId = "clubId";
        AffiliationStatus newStatus = AffiliationStatus.ACCEPTED;

        doNothing().when(clubService).updateClubStatus(clubId, newStatus);

        mockMvc.perform(post("/api/club/update-status/{id}/{newStatus}", clubId, newStatus)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}