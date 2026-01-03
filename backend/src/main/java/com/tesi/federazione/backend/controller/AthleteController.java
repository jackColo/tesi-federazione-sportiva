package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.mapper.UserMapper;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.service.AthleteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/athlete")
public class AthleteController {

    private final AthleteService athleteService;

    public AthleteController(AthleteService athleteService, UserMapper userMapper) {
        this.athleteService = athleteService;
    }

    @GetMapping("/to-approve/{id}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<List<AthleteDTO>> getAthletesToApprove(@PathVariable String clubId) {
        List<AthleteDTO> athleteDTOs = athleteService.getAthletesByStatusAndClubId(AffiliationStatus.SUBMITTED, clubId);

        return new ResponseEntity<>(athleteDTOs, HttpStatus.OK);
    }

    @PostMapping("/update-status/{id}/{newStatus}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<Void> updateAthleteStatus(@PathVariable String id, @PathVariable AffiliationStatus newStatus) {
        athleteService.updateStatus(id, newStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/renew-submission/{id}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<Void> renewAthleteSubmissionStatus(@PathVariable String id) {
        athleteService.updateStatus(id, AffiliationStatus.SUBMITTED);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/club/{clubId}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<List<AthleteDTO>> getAthleteByClubId(@PathVariable String clubId) {
        List<AthleteDTO> athletesDTO = athleteService.getAthletesByClubId(clubId);

        return new ResponseEntity<>(athletesDTO, HttpStatus.OK);
    }


    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<List<AthleteDTO>> getAllAthletes() {
        List<AthleteDTO> athleteDTOs = athleteService.getAllAthletes();

        return new ResponseEntity<>(athleteDTOs, HttpStatus.OK);
    }
}
