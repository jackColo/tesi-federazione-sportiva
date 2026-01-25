package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione delle operazioni sugli oggetti USER
 * Gestisce il recupero delle informazioni, la creazione e la modifica dei profili utente.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    /**
     * Recupera un utente tramite il suo indirizzo email.
     *
     * @param email Email dell'utente da cercare.
     * @return ResponseEntity<UserDTO> DTO dell'utente trovato e HttpStatus.
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER', 'ATHLETE')")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        log.info("Richiesta recupero utente {}", email);
        UserDTO userDTO = userService.getUserByEmail(email);
        log.info("Utente {} trovato", userDTO.getEmail());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * Recupera un utente tramite il suo ID.
     *
     * @param id L'ID dell'utente da cercare.
     * @return ResponseEntity<UserDTO> DTO dell'utente trovato e HttpStatus.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER', 'ATHLETE')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        log.info("Richiesta recupero utente {}", id);
        UserDTO userDTO = userService.getUserById(id);
        log.info("Utente {} recuperato con successo", userDTO.getEmail());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * Crea un nuovo utente.
     *
     * @param createUserDTO DTO contenente i dati per la creazione.
     * @return ResponseEntity<UserDTO> Utente creato e HttpStatus.
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER')")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserDTO createUserDTO) {
        log.info("Inizio creazione nuovo utente {}", createUserDTO.getEmail());
        UserDTO userDTO = userService.createUser(createUserDTO);
        log.info("Utente {} creato con successo.", userDTO.getEmail());
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    /**
     * Aggiorna i dati di un utente esistente.
     * Accessibile a FEDERATION_MANAGER, CLUB_MANAGER e ATHLETE.
     *
     * @param id L'ID dell'utente da aggiornare (preso dall'URL).
     * @param createUserDTO DTO con i nuovi dati.
     * @return ResponseEntity<UserDTO> L'utente aggiornato e HttpStatus OK.
     */
    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER', 'ATHLETE')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody CreateUserDTO createUserDTO) {
        log.info("Richiesta aggiornamento dati per l'utente {}", id);
        UserDTO userDTO = userService.updateUser(createUserDTO);
        log.info("Dati utente {} aggiornati con successo.", id);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

}
