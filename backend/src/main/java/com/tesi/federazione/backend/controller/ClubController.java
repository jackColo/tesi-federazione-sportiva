package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller per l'accesso del client ai metodi che gestiscono il club.
 * Utilizza RESP API
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/club")
public class ClubController {

    private final ClubService clubService;

    /**
     * Endpoint per creare un nuovo club
     * @param createClubDTO DTO contenente i dati minimi necessari a creare un club
     * @return ResponseEntity<ClubDTO> Club creato a DB e HTTPStatus
     */
    @PostMapping("/create")
    public ResponseEntity<ClubDTO> createClub(@RequestBody CreateClubDTO createClubDTO) {
        ClubDTO clubDTO = clubService.createClub(createClubDTO);
        return new ResponseEntity<>(clubDTO, HttpStatus.CREATED);
    }

    /**
     * Enpoint per aggiornare i dati di un club
     * @param updateClub DTO contenente i nuovi dati del club
     * @return ResponseEntity<ClubDTO> Club modificato e HTTPStatus
     */
    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<ClubDTO> updateClub(@RequestBody UpdatedClubDTO updateClub ) {
        ClubDTO clubDTO = clubService.updateClub(updateClub);
        return new ResponseEntity<>(clubDTO, HttpStatus.OK);
    }

    /**
     * Endpoint per recuperare un club tramite id
     * @param id Id del club da cercare
     * @return ResponseEntity<ClubDTO> Club richiesto e HTTPStatus
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER', 'ATHLETE')")
    public ResponseEntity<ClubDTO> getClubById(@PathVariable String id) {
        ClubDTO clubDTO = clubService.getClubById(id);
        return new ResponseEntity<>(clubDTO, HttpStatus.OK);
    }

    /**
     * Enpoint per recuperare tutti i club che devono essere approvati
     * @return ResponseEntity<List<ClubDTO>> Elenco dei club da approvare e HTTPStatus
     */
    @GetMapping("/to-approve")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<List<ClubDTO>> getClubsToApprove() {
        List<ClubDTO> clubDTOs = clubService.getClubsByStatus(AffiliationStatus.SUBMITTED);
        return new ResponseEntity<>(clubDTOs, HttpStatus.OK);
    }

    /**
     * Enpoint per recuperare tutti i club
     * @return ResponseEntity<List<ClubDTO>> Elenco dei club e HTTPStatus
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER', 'ATHLETE')")
    public ResponseEntity<List<ClubDTO>> getAllClubs() {
        List<ClubDTO> clubDTOs = clubService.getAll();
        return new ResponseEntity<>(clubDTOs, HttpStatus.OK);
    }

    /**
     * Enpoint per riportare lo stato di affiliazione di un club allo stato SUBMITTED
     * Utile per ripristinare il club dagli stati EXPIRED o REJECTED
     * @param id Id del club di cui ripristinare lo stato
     * @return ResponseEntity<Void> HTTPStatus
     */
    @PostMapping("/renew-submission/{id}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<Void> renewClubAffiliationStatus(@PathVariable String id) {
        clubService.updateClubStatus(id, AffiliationStatus.SUBMITTED);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Enpoint per modificare lo stato di affiliazione di un club
     * @param id Id del club di cui modificare lo stato
     * @param newStatus Nuovo stato di affiliazione per il club
     * @return ResponseEntity<Void> HTTPStatus
     */
    @PostMapping("/update-status/{id}/{newStatus}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<Void> updateAffiliationStatus(@PathVariable String id, @PathVariable AffiliationStatus newStatus) {
        clubService.updateClubStatus(id, newStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
