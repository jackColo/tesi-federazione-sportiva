package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.user.ChangePasswordRequestDTO;
import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.security.RequiresClubApproval;
import com.tesi.federazione.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @RequiresClubApproval
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
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * Aggiorna la password di un utente esistente
     *
     * @param id L'ID dell'utente di cui si vuole aggiornare la password.
     * @param request ChangePasswordRequestDTO contenente la vecchia e la nuova password.
     * @return ResponseEntity<UserDTO> L'utente aggiornato e HttpStatus OK.
     */
    @PostMapping("/change-password/{id}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER', 'CLUB_MANAGER', 'ATHLETE')")
    public ResponseEntity<Void> changeUserPassword(@PathVariable String id, @RequestBody ChangePasswordRequestDTO request) {
        log.info("Richiesta cambio password per l'utente {}", id);
        userService.changeUserPassword(id, request.getOldPassword(), request.getNewPassword());
        log.info("Password utente {} aggiornata con successo.", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Aggiorna la password di un utente esistente
     *
     * @param role L'ID dell'utente di cui si vuole aggiornare la password.
     * @return ResponseEntity<UserDTO> L'utente aggiornato e HttpStatus OK.
     */
    @GetMapping("/find-by-role/{role}")
    @PreAuthorize("hasAnyAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<List<UserDTO>> findByRole(@PathVariable Role role) {
        log.info("Richiesta dell'elenco di utenti con ruolo {}", role);
        List<UserDTO> users = userService.getAllByRole(role);
        log.info("Trovati {} utenti con ruolo {}.", users.size(), role);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
