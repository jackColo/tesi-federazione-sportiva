package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.ClubDTO;
import com.tesi.federazione.backend.dto.CreateClubDTO;
import com.tesi.federazione.backend.mapper.ClubMapper;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.service.ClubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/club")
public class ClubController {

    private final ClubService clubService;
    private final ClubMapper clubMapper;

    public ClubController(ClubService clubService, ClubMapper clubMapper) {
        this.clubService = clubService;
        this.clubMapper = clubMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<ClubDTO> createClub(@RequestBody CreateClubDTO createClubDTO) {
        Club club = clubService.createClub(createClubDTO);
        ClubDTO clubDTO = clubMapper.toDTO(club);
        return new ResponseEntity<>(clubDTO, HttpStatus.CREATED);
    }
}
