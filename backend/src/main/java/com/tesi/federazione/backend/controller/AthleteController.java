package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.mapper.UserMapper;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.service.AthleteService;
import com.tesi.federazione.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/athlete")
public class AthleteController {

    private final AthleteService athleteService;
    private final UserService userService;
    private final UserMapper userMapper;

    public AthleteController(AthleteService athleteService, UserService userService, UserMapper userMapper) {
        this.athleteService = athleteService;
        this.userService = userService;
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
}
