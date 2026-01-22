package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.service.AthleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller per la gestione delle operazioni sugli oggetti ATHLETE
 * Gestisce il recupero delle informazioni e i cambi dello stato d'affiliazione.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/athlete")
@Slf4j
public class AthleteController {

    private final AthleteService athleteService;

    /**
     * Recupera la lista degli atleti di uno specifico club in attesa di approvazione.
     * Accessibile solo al FEDERATION_MANAGER.
     *
     * @param clubId ID del club di cui visualizzare le richieste.
     * @return ResponseEntity<List<AthleteDTO>> Lista di AthleteDTO in attesa di approvazione e HttpStatus.
     */
    @GetMapping("/to-approve/{id}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<List<AthleteDTO>> getAthletesToApprove(@PathVariable String clubId) {
        log.info("Ricerca di tutti gli utenti da approvare per il club {}", clubId);
        List<AthleteDTO> athleteDTOs = athleteService.getAthletesByStatusAndClubId(AffiliationStatus.SUBMITTED, clubId);
        log.info("Trovati {} atleti per il club {}", athleteDTOs.size(), clubId);
        return new ResponseEntity<>(athleteDTOs, HttpStatus.OK);
    }

    /**
     * Aggiorna lo stato di affiliazione di un atleta
     * Accessibile solo al FEDERATION_MANAGER.
     *
     * @param id ID dell'atleta.
     * @param newStatus Nuovo stato da assegnare (con formato dell'enum AffiliationStatus)
     */
    @PostMapping("/update-status/{id}/{newStatus}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<Void> updateAthleteStatus(@PathVariable String id, @PathVariable AffiliationStatus newStatus) {
        log.info("Richiesta di modifica stato di affiliazione per l'utente {} -> nuovo stato {}", id, newStatus);
        athleteService.updateStatus(id, newStatus);
        log.info("Stato d'affiliazione per l'utente {} modificato con successo", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Rinnova la sottomissione di un atleta, riportando lo stato a SUBMITTED.
     * Utile se un atleta era stato rifiutato e il club vuole riproporlo dopo aver corretto i dati.
     * Accessibile sia al Club Manager che alla Federazione, per il club manager viene verificata
     * l'appartenenza dell'atleta al club gestito dal manager.
     *
     * @param id ID dell'atleta.
     */
    @PostMapping("/renew-submission/{id}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<Void> renewAthleteSubmissionStatus(@PathVariable String id) {
        log.info("Richiesta di rinnovo affiliazione per l'utente {}", id);
        athleteService.updateStatus(id, AffiliationStatus.SUBMITTED);
        log.info("Richiesta d'affiliazione rinnovata per l'utente {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Recupera tutti gli atleti appartenenti a uno specifico club.
     * Accessibile sia al Club Manager che alla Federazione, per il club manager viene
     * verificato che il club indicato sia gestito dal manager che effettua la richiesta.
     *
     * @param clubId ID del club.
     * @return ResponseEntity<List<AthleteDTO>> Lista completa degli atleti del club.
     */
    @GetMapping("/club/{clubId}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<List<AthleteDTO>> getAthleteByClubId(@PathVariable String clubId) {
        log.info("Recuperando tutti gli atleti del club {} ...", clubId);
        List<AthleteDTO> athletesDTO = athleteService.getAthletesByClubId(clubId);
        log.info("Trovati {} atleti per il club {}", athletesDTO.size(), clubId);
        return new ResponseEntity<>(athletesDTO, HttpStatus.OK);
    }

    /**
     * Recupero l'elenco di tutti gli atleti presenti a DB.
     * Accessibile sia al Club Manager che alla Federazione, per il club manager l'elenco viene
     * filtrato per l'id del club a cui appartiene
     *
     * @return ResponseEntity<List<AthleteDTO>> Lista completa degli atleti trovati
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<List<AthleteDTO>> getAllAthletes() {
        log.info("Recuperando l'elenco di tutti gli atleti...");
        List<AthleteDTO> athleteDTOs = athleteService.getAllAthletes();
        log.info("Trovati {} atleti", athleteDTOs.size());
        return new ResponseEntity<>(athleteDTOs, HttpStatus.OK);
    }
}
