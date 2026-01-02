package com.tesi.federazione.backend.controller;

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
    private final UserMapper userMapper;

    public AthleteController(AthleteService athleteService, UserMapper userMapper) {
        this.athleteService = athleteService;
        this.userMapper = userMapper;
    }

    @GetMapping("/to-approve/{id}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<List<UserDTO>> getAthletesToApprove(@PathVariable String clubId) {
        List<Athlete> athletes = athleteService.getAthletesByStatusAndClubId(AffiliationStatus.SUBMITTED, clubId);

        List<UserDTO> athleteDTOS = athletes.stream()
                .map(userMapper::toDTO)
                .toList();

        return new ResponseEntity<>(athleteDTOS, HttpStatus.OK);
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<Void> approveAthlete(@PathVariable String id) {
        athleteService.approveAthlete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/club/{clubId}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<List<UserDTO>> getAthleteByClubId(@PathVariable String clubId) {
        List<Athlete> athletes = athleteService.getAthletesByClubId(clubId);

        List<UserDTO> athletesDTO = athletes.stream()
                .map(userMapper::toDTO)
                .toList();

        return new ResponseEntity<>(athletesDTO, HttpStatus.OK);
    }


    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<List<UserDTO>> getAllAthletes() {
        List<Athlete> users = athleteService.getAllAthletes();

        List<UserDTO> usersDTO = users.stream()
                .map(userMapper::toDTO)
                .toList();

        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }
}
