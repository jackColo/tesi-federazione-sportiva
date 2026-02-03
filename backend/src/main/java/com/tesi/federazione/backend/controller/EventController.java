package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller per la gestione di "Event" ed "Enrollment".
 * Gestisce recupero dati, creazione, modifica e aggiornamento stato dei eventi e iscrizioni.
 * La manipolazione degli eventi è concessa solo ai DEFERATION_MANAGER;
 * La manipolazione delle iscrizioni è concessa a FEDERATION_MANAGER e GLUB_MANAGER.
 * Gli ATHELTE possono creare iscrizioni ma solo in stato "DRAFT".
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    /**
     * Recupera la lista di tutti gli eventi presenti nel sistema.
     * Endpoint pubblico.
     *
     * @return Lista di tutti gli eventi e HttpStatus
     */
    @GetMapping("/all")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        log.info("Recupero lista completa di tutti gli eventi");
        List<EventDTO> allEvents = eventService.getAllEvents();
        log.info("Recuperati {} eventi", allEvents.size());
        return new ResponseEntity<>(allEvents, HttpStatus.OK);
    }

    /**
     * Recupera il dettaglio di un singolo evento tramite ID.
     * Endpoint pubblico.
     *
     * @param id ID dell'evento.
     * @return DTO dell'evento e HttpStatus OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable String id) {
        log.info("Richiesta dettaglio evento {}", id);
        EventDTO eventDTO = eventService.getEventById(id);
        log.info("Evento {} recuperato", id);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    /**
     * Modifica lo stato di un evento.
     * Accessibile solo ai FEDERATION_MANAGER
     *
     * @param id ID dell'evento.
     * @param newState Nuovo stato da applicare.
     */
    @PatchMapping("/update-state/{id}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<Void> updateEvent(@PathVariable String id, @RequestBody EventStatus newState) {
        log.info("Richiesta cambio stato evento {} -> Nuovo stato: {}", id, newState);
        eventService.updateEventState(id, newState);
        log.info("Stato evento {} aggiornato con successo.", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Crea un nuovo evento.
     * Accessibile solo ai FEDERATION_MANAGER.
     *
     * @param event DTO con i dati per la creazione.
     * @return L'evento creato e HttpStatus.
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<EventDTO> createNewEvent(@RequestBody CreateEventDTO event) {
        log.info("Richiesta creazione nuovo evento: '{}'}", event.getName());
        EventDTO newEvent = eventService.createEvent(event);
        log.info("Evento {} creato con successo.", event.getName());
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    /**
     * Aggiorna i dati di un evento esistente.
     * Accessibile solo ai FEDERATION_MANAGER.
     *
     * @param newEventData DTO con i dati aggiornati.
     * @return L'evento aggiornato e HttpStatus.
     */
    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<EventDTO> updatedEvent(@RequestBody EventDTO newEventData) {
        log.info("Richiesta aggiornamento dati evento {}", newEventData.getId());
        EventDTO event = eventService.updateEvent(newEventData);
        log.info("Evento {} aggiornato con successo.", newEventData.getId());
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    //
    // SEZIONE ISCRIZIONI
    //

    /**
     * Iscrizione di un atleta a un evento.
     * Accessibile a tutti gli utenti autenticati, gli atleti possono creare iscrizioni solo in stato "DRAFT".
     * Controlli sui permessi:
     * - Un manager può iscrivere solo atleti del suo club
     * - Un atleta può iscrivere solo sè stesso
     *
     * @param request DTO con ID evento e ID atleta.
     * @return L'iscrizione creata e HttpStatus.
     */
    @PostMapping("/enroll")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER','ATHLETE')")
    public ResponseEntity<EnrollmentDTO> enrollAthlete(
            @RequestBody CreateEnrollmentDTO request) {
        log.info("Richiesta iscrizione atleta {} all'evento {}", request.getAthleteId(), request.getEventId());
        EnrollmentDTO newEnrollment = eventService.enrollAthlete(request);
        log.info("Iscrizione completata con successo.");
        return new ResponseEntity<>(newEnrollment, HttpStatus.CREATED);
    }

    /**
     * Recupera il dettaglio di una singola iscrizione.
     * Controlli sui permessi:
     * - Un manager può vedere le iscrizioni solo di atleti del suo club
     * - Un atleta può vedere solo le proprie iscrizioni
     *
     * @param id ID dell'iscrizione.
     * @return DTO dell'iscrizione e HttpStatus.
     */
    @GetMapping("/enroll/{id}")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER','ATHLETE')")
    public ResponseEntity<EnrollmentDTO> getEnrollment(@PathVariable String id) {
        log.info("Recupero iscrizione {}", id);
        EnrollmentDTO newEnrollment = eventService.getEnrollment(id);
        log.info("Iscrizione {} recuperata con successo.", id);
        return new ResponseEntity<>(newEnrollment, HttpStatus.OK);
    }

    /**
     * Recupera la lista delle iscrizioni per un determinato evento, con filtri opzionali.
     * Controlli sui permessi:
     * - Un manager può visualizzare solo le iscrizioni di atleti del suo club
     * - Un atleta può visualizzare solo le proprie iscrizioni.
     *
     * @param eventId ID dell'evento (Obbligatorio).
     * @param clubId (Opzionale) Filtra per club.
     * @param athleteId (Opzionale) Filtra per singolo atleta.
     * @return Lista di iscrizioni e HttpStatus.
     */
    @GetMapping("/enroll-all/{eventId}")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER', 'ATHLETE')")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByEventId(@PathVariable String eventId, @RequestParam(required = false) String clubId,
                                                                       @RequestParam(required = false) String athleteId) {
        log.info("Recupero iscrizioni per evento {}", eventId);
        List<EnrollmentDTO> enrollments = eventService.getEnrollmentsByEventId(eventId, clubId, athleteId);
        log.info("Trovate {} iscrizioni", enrollments.size());
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    /**
     * Aggiorna un'iscrizione esistente.
     * Controlli sui permessi:
     * - Un manager può aggiornare le iscrizioni solo di atleti del suo club
     * - Un atleta può aggiornare solo le proprie iscrizioni e solo se sono ancora in bozza.
     *
     * @param request DTO con i dati aggiornati.
     * @return L'iscrizione aggiornata e HttpStatus.
     */
    @PatchMapping("/enroll/update")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER','ATHLETE')")
    public ResponseEntity<EnrollmentDTO> updatedEnrollment(@RequestBody EnrollmentDTO request) {
        log.info("Richiesta aggiornamento iscrizione {}", request.getId());
        EnrollmentDTO newEnrollment = eventService.updateEnrollment(request);
        log.info("Iscrizione {} aggiornata con successo.", request.getId());
        return new ResponseEntity<>(newEnrollment, HttpStatus.OK);
    }

    /**
     * Aggiorna lo stato di un'iscrizione esistente.
     * Controlli sui permessi:
     * - Un manager può aggiornare lo stato dell'iscrizioni solo di atleti del suo club
     * - Solo il federation manager può impostare gli stati "APPROVED" e "REJECTED"
     *
     * @param id ID dell'iscrizione di cui si vuole aggiornare lo stato.
     * @param newStatus Nuovo stato da associare all'iscrizione
     * @return DTO dell'iscrizione con lo stato modificato.
     */
    @PatchMapping("/enroll/update-status/{id}")
    @PreAuthorize("hasAnyAuthority('CLUB_MANAGER', 'FEDERATION_MANAGER')")
    public ResponseEntity<EnrollmentDTO> updatedEnrollmentStatus(@PathVariable String id, @RequestBody EnrollmentStatus newStatus) {
        log.info("Richiesta aggiornamento stato iscrizione {}", id);
        EnrollmentDTO newEnrollment = eventService.updateEnrollmentStatus(id, newStatus);
        log.info("Stato iscrizione {} aggiornato con successo.", id);
        return new ResponseEntity<>(newEnrollment, HttpStatus.OK);
    }
}