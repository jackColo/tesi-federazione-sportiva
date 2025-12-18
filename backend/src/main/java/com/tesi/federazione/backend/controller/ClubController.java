package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.user.CreateClubDTO;
import com.tesi.federazione.backend.mapper.ClubMapper;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.service.ClubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/to-approve")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<List<ClubDTO>> getClubsToApprove() {
        List<Club> clubs = clubService.getClubsToApprove();

        List<ClubDTO> clubDTOS = clubs.stream()
                .map(clubMapper::toDTO)
                .toList();

        return new ResponseEntity<>(clubDTOS, HttpStatus.OK);
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<Void> approveClub(@PathVariable String id) throws Exception {
        clubService.approveClub(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
