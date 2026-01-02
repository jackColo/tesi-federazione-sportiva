package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.mapper.ClubMapper;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
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

    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER', 'ATHLETE')")
    public ResponseEntity<ClubDTO> updateClub(@RequestBody UpdatedClubDTO updateClub ) {
        Club club = clubService.updateClub(updateClub);
        ClubDTO clubDTO = clubMapper.toDTO(club);
        return new ResponseEntity<>(clubDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER', 'ATHLETE')")
    public ResponseEntity<ClubDTO> getClubById(@PathVariable String id) {
        Club club = clubService.getClubById(id).orElseThrow(() -> new ResourceNotFoundException("Club con ID " + id + " non trovato"));

        ClubDTO clubDTO = clubMapper.toDTO(club);
        return new ResponseEntity<>(clubDTO, HttpStatus.OK);
    }

    @GetMapping("/to-approve")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<List<ClubDTO>> getClubsToApprove() {
        List<Club> clubs = clubService.getClubsByStatus(AffiliationStatus.SUBMITTED);

        List<ClubDTO> clubDTOS = clubs.stream()
                .map(clubMapper::toDTO)
                .toList();

        return new ResponseEntity<>(clubDTOS, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER', 'ATHLETE')")
    public ResponseEntity<List<ClubDTO>> getAllClubs() {
        List<Club> clubs = clubService.getAll();

        List<ClubDTO> clubDTOS = clubs.stream()
                .map(clubMapper::toDTO)
                .toList();

        return new ResponseEntity<>(clubDTOS, HttpStatus.OK);
    }

    @PostMapping("/renew-submission/{id}/{newStatus}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<Void> renewClubAffiliationStatus(@PathVariable String id, @PathVariable AffiliationStatus newStatus) {
        clubService.updateClubStatus(id, newStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/update-status/{id}/{newStatus}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<Void> updateAffiliationStatus(@PathVariable String id, @PathVariable AffiliationStatus newStatus) {
        clubService.updateClubStatus(id, newStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
