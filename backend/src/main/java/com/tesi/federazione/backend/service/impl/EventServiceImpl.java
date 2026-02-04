package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.exception.ResourceConflictException;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.exception.UnauthorizedException;
import com.tesi.federazione.backend.factory.state.EventStateFactory;
import com.tesi.federazione.backend.mapper.EnrollmentMapper;
import com.tesi.federazione.backend.mapper.EventMapper;
import com.tesi.federazione.backend.model.Enrollment;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import com.tesi.federazione.backend.model.enums.EventStatus;
import com.tesi.federazione.backend.repository.EnrollmentRepository;
import com.tesi.federazione.backend.repository.EventRepository;
import com.tesi.federazione.backend.security.SecurityUtils;
import com.tesi.federazione.backend.service.ClubService;
import com.tesi.federazione.backend.service.EventService;
import com.tesi.federazione.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio EventService.
 * Implementa i controlli di sicurezza puntuali (tramite SecurityUtils) per garantire che
 * Club Manager e Atleti possano operare solo sui dati di loro competenza.
 * Sfrutta il design pattern "State" applicato allo stato dell'evento per gestire i passaggi di stato
 * e la possibilità di iscriversi all'evento.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EventMapper eventMapper;
    private final EnrollmentMapper enrollmentMapper;

    private final ClubService clubService;
    private final UserService userService;

    private final SecurityUtils securityUtils;

    /**
     * Recupera la lista di tutti gli eventi presenti a DB.
     * @return Lista completa di EventDTO.
     */
    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recupera il dettaglio di un singolo evento tramite ID.
     * Restituisce anche il numero corrente di iscritti.
     *
     * @param id ID dell'evento.
     * @return DTO dell'evento popolato.
     * @throws ResourceNotFoundException Se l'evento non viene trovato.
     */
    @Override
    public EventDTO getEventById(String id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + id + " non trovato"));
        long approvedCount = enrollmentRepository.findByEventIdAndStatus(id, EnrollmentStatus.APPROVED).size();
        EventDTO dto = eventMapper.toDTO(event);
        dto.setEnrolledCount(approvedCount);

        return dto;
    }

    /**
     * Crea un nuovo evento nel sistema.
     *
     * @param createEventDTO DTO con i dati di creazione.
     * @return DTO dell'evento creato.
     */
    @Override
    public EventDTO createEvent(CreateEventDTO createEventDTO) {
        Event event = new Event();
        event.setName(createEventDTO.getName());
        event.setDescription(createEventDTO.getDescription());
        event.setLocation(createEventDTO.getLocation());
        event.setDate(createEventDTO.getDate());
        event.setRegistrationOpenDate(createEventDTO.getRegistrationOpenDate());
        event.setRegistrationCloseDate(createEventDTO.getRegistrationCloseDate());
        event.setDisciplines(createEventDTO.getDisciplines());

        event.setStatus(EventStatus.SCHEDULED);

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toDTO(savedEvent);
    }

    /**
     * Aggiorna i dati di un evento esistente.
     *
     * @param eventDTO DTO con i dati dell'evento da aggiornare.
     * @return DTO dell'evento modificato.
     * @throws ResourceNotFoundException Se l'evento non viene trovato.
     */
    @Override
    public EventDTO updateEvent(EventDTO eventDTO) {
        Event oldEvent = eventRepository.findById(eventDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + eventDTO.getId() + " non trovato"));

        Event newEvent = new Event();
        newEvent.setName(eventDTO.getName());
        newEvent.setDescription(eventDTO.getDescription());
        newEvent.setLocation(eventDTO.getLocation());
        newEvent.setDate(eventDTO.getDate());
        newEvent.setRegistrationOpenDate(eventDTO.getRegistrationOpenDate());
        newEvent.setRegistrationCloseDate(eventDTO.getRegistrationCloseDate());
        newEvent.setDisciplines(eventDTO.getDisciplines());

        newEvent.setStatus(oldEvent.getStatus());
        newEvent.setId(eventDTO.getId());

        Event savedEvent = eventRepository.save(newEvent);

        return eventMapper.toDTO(savedEvent);
    }

    /**
     * Modifica lo stato di un evento applicando il design pattern STATE per la
     * gestione delle transizioni da uno stato all'altro.
     *
     * @param id ID dell'evento.
     * @param newStatus Nuovo stato da applicare.
     * @throws ResourceNotFoundException Se l'evento non esiste.
     * @throws ActionNotAllowedException Se lo stato richiesto non è gestito.
     */
    @Override
    public void updateEventState(String id, EventStatus newStatus) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + id + " non trovato"));

        if (newStatus == null) {
            throw new ActionNotAllowedException("Il nuovo stato non può essere null");
        }

        event.setState(EventStateFactory.getInitialState(event.getStatus()));

        log.info("Evento {} transizione stato: {} -> {}", id, event.getStatus(), newStatus);

        switch (newStatus) {
            case SCHEDULED:
                event.resumeEvent();
                restoreEventEnrollments(id);
                break;
            case COMPLETED:
                event.completeEvent();
                break;
            case CANCELLED:
                event.cancelEvent();
                cancelEventEnrollments(id);
                break;
            case REGISTRATION_CLOSED:
                event.closeRegistrations();
                break;
            case REGISTRATION_OPEN:
                event.openRegistrations();
                break;
            default:
                log.error("Lo stato {} non è gestito", newStatus);
                throw new ActionNotAllowedException("Stato non gestito: " + newStatus);
        }

        eventRepository.save(event);
    }

    /**
     * Iscrive un atleta a un evento.
     * Include controlli di sicurezza:
     * - I Club Manager possono iscrivere solo i propri atleti.
     * - Gli Atleti possono solo creare una bozza per la propria iscrizione.
     *
     * @param enrollmentDTO DTO con i dati per l'iscrizione.
     * @return DTO dell'iscrizione creata.
     * @throws UnauthorizedException Se i controlli di sicurezza falliscono.
     * @throws ResourceConflictException Se risulta già presente un'altra iscrizione per lo stesso evento, atleta e disciplina
     */
    @Override
    public EnrollmentDTO enrollAthlete(CreateEnrollmentDTO enrollmentDTO) {

        // Controllo di sicurezza per i Club Manager: può iscrivere solo atleti del proprio club
        if (securityUtils.isClubManager() && !securityUtils.isMyClub(enrollmentDTO.getClubId())) {
            log.error("Un Club Manager può iscrivere solo atleti del suo club");
            throw new UnauthorizedException("Non puoi iscrivere atleti per conto di altri club.");
        }

        // Controllo si sicurezza per gli Atleti: può iscrivere solo se stesso
        if (securityUtils.isAthlete()) {
            if (!securityUtils.getCurrentUserId().equals(enrollmentDTO.getAthleteId())) {
                log.error("Un Atleta può iscrivere solo sè stesso");
                throw new UnauthorizedException("Non puoi effettuare iscrizioni per altri atleti.");
            } else if (!enrollmentDTO.isDraft()) {
                log.error("Un Atleta può creare solo una bozza della sua iscrizione.");
                throw new UnauthorizedException("Puoi creare solo una bozza dell'iscrizione!");
            }
        }

        //Verifica duplicati
        if (enrollmentRepository.existsByEventIdAndAthleteIdAndDiscipline(enrollmentDTO.getEventId(), enrollmentDTO.getAthleteId(), enrollmentDTO.getCompetitionType())) {
            log.error("Atleta {} già iscritto per la disciplina {}", enrollmentDTO.getAthleteId(), enrollmentDTO.getCompetitionType());
            throw new ResourceConflictException("Atleta già iscritto all'evento " + enrollmentDTO.getEventId() + " per la disciplina " + enrollmentDTO.getCompetitionType());
        }

        // Controlli di sicurezza: se non esistono club, atleta o evento con questi ID i service lanciano un errore.
        String clubName = clubService.getClubById(enrollmentDTO.getClubId()).getName();
        userService.getUserById(enrollmentDTO.getAthleteId());
        Event event = eventRepository.findById(enrollmentDTO.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + enrollmentDTO.getEventId() + " non trovato"));

        // Valido la possibilità di effettuare la registrazione
        event.setState(EventStateFactory.getInitialState(event.getStatus()));
        event.validateRegistration(enrollmentDTO.isDraft(), securityUtils.isFederationManager());

        Enrollment enrollment = new Enrollment();
        enrollment.setAthleteId(enrollmentDTO.getAthleteId());
        enrollment.setAthleteClubName(clubName);
        enrollment.setAthleteFirstname(enrollmentDTO.getAthleteFirstname());
        enrollment.setAthleteLastname(enrollmentDTO.getAthleteLastname());
        enrollment.setAthleteWeight(enrollmentDTO.getAthleteWeight());
        enrollment.setAthleteHeight(enrollmentDTO.getAthleteHeight());
        enrollment.setAthleteGender(enrollmentDTO.getAthleteGender());
        enrollment.setAthleteAffiliationStatus(enrollmentDTO.getAthleteAffiliationStatus());
        enrollment.setAthleteMedicalCertificateExpireDate(enrollmentDTO.getAthleteMedicalCertificateExpireDate());

        enrollment.setEventId(event.getId());
        enrollment.setClubId(enrollmentDTO.getClubId());
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setDiscipline(enrollmentDTO.getCompetitionType());
        if (enrollmentDTO.isDraft()) {
            enrollment.setStatus(EnrollmentStatus.DRAFT);
        } else {
            enrollment.setStatus(EnrollmentStatus.SUBMITTED);
        }

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDTO(savedEnrollment);
    }

    /**
     * Recupera le iscrizioni di un evento, con filtri opzionali per club o atleta.
     * Se l'utente è un Club Manager o un Atleta, i filtri vengono forzati per
     * limitare la visibilità ai soli dati di loro competenza.
     *
     * @param eventId ID dell'evento (Obbligatorio).
     * @param clubId ID del club (Opzionale).
     * @param athleteId ID dell'atleta (Opzionale).
     * @return Lista di iscrizioni che corrispondono ai criteri.
     * @throws UnauthorizedException Se l'utente prova a impostare filtri per dati a cui non ha accesso.
     */
    @Override
    public List<EnrollmentDTO> getEnrollmentsByEventId(String eventId, String clubId, String athleteId) {
        List<Enrollment> enrollments;

        // Controllo i permessi di accesso dei club manager e forzo il clubId se non è impostato
        if (securityUtils.isClubManager()) {
            String myClubId = securityUtils.getUserClub();
            if (clubId != null && !clubId.equals(myClubId)) {
                log.error("Un manager può visualizzare solo le iscrizioni peg gli atleti del proprio club");
                throw new UnauthorizedException("Non puoi visualizzare le iscrizioni di atleti di altri club.");
            } else if (clubId == null) {
                clubId = myClubId;
            }
        }

        // Controllo i permessi di accesso dell'atleta e forzo l'atheleteId se non è impostato
        if (securityUtils.isAthlete()) {
            String myId = securityUtils.getCurrentUserId();
            if (athleteId != null && !athleteId.equals(myId)) {
                log.error("Un atleta può visualizzare solo le proprie iscrizioni.");
                throw new UnauthorizedException("Non puoi visualizzare le iscrizioni di altri atleti.");
            } else if (athleteId == null) {
                athleteId = myId;
            }
        }

        if (athleteId != null) {
            enrollments = enrollmentRepository.findByEventIdAndAthleteId(eventId, athleteId);
        } else if (clubId != null) {
            enrollments = enrollmentRepository.findByEventIdAndClubId(eventId, clubId);
        } else {
            enrollments = enrollmentRepository.findByEventId(eventId);
        }

        return enrollments.stream().map(enrollmentMapper::toDTO).toList();
    }

    /**
     * Recupera tutte le iscrizioni accettate di un determinato evento
     *
     * @param eventId ID dell'evento.
     * @return Lista di iscrizioni accettate relative all'evento indicato.
     */
    @Override
    public List<EnrollmentDTO> getApprovedEnrollmentsByEventId(String eventId) {
        List<Enrollment> enrollments = enrollmentRepository.findByEventIdAndStatus(eventId, EnrollmentStatus.APPROVED);
        return enrollments.stream().map(enrollmentMapper::toDTO).toList();
    }

    /**
     * Recupera una singola iscrizione per ID.
     * Controlli sui permessi:
     * - Un manager può visualizzare solo le iscrizioni di atleti del suo club
     * - Un atleta può visualizzare solo le proprie iscrizioni.
     *
     * @param id ID dell'iscrizione.
     * @return DTO dell'iscrizione.
     * @throws UnauthorizedException Se l'utente non ha i permessi per vedere l'iscrizione richiesta.
     */
    @Override
    public EnrollmentDTO getEnrollment(String id) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Iscrizione con id " + id + " non trovata."));

        // Controlli di sicurezza
        if (securityUtils.isClubManager() && !securityUtils.isMyClub(enrollment.getClubId())) {
            log.error("Un manager può visualizzare solo le iscrizioni peg gli atleti del proprio club");
            throw new UnauthorizedException("Non puoi visualizzare le iscrizioni di atleti di altri club.");
        }
        if (securityUtils.isAthlete() && !securityUtils.getCurrentUserId().equals(enrollment.getAthleteId())) {
            log.error("Un atleta può visualizzare solo le proprie iscrizioni.");
            throw new UnauthorizedException("Non puoi visualizzare le iscrizioni di altri atleti.");
        }
        return enrollmentMapper.toDTO(enrollment);
    }

    /**
     * Implementazione del metodo per aggiornare i dati di un iscrizione, permette anche di aggiornare contestualmente anche lo stato.
     * Il ruolo del richiedente deve permettere di eseguire la modifica o la transizione di stato indicata:
     * - Atleta nessuna transizione di stato; modifica solo per le proprie iscrizioni in bozza
     * - Club Manager transizione di stato solo verso gli stati "DRAFT", "SUBMITTED" e "RETIRED"; modifica solo per le iscrizioni dei propri atleti
     *
     * @param enrollmentDTO DTO con i dati aggiornati.
     * @return EnrollmentDTO dell'iscrizione aggiornata
     * @throws UnauthorizedException     Se il ruolo del richiedente non permette di operare sull'iscrizione indicata
     * @throws ResourceNotFoundException Se l'iscrizione o l'evento a cui si riferisce non vengono trovati nel database
     * @throws ActionNotAllowedException Se il ruolo del richiedente non permette la transizione di stato richiesta
     * @throws ResourceConflictException Se risulta già presente un'altra iscrizione per lo stesso evento, atleta e disciplina
     */
    @Override
    @Transactional
    public EnrollmentDTO updateEnrollment(EnrollmentDTO enrollmentDTO) {

        // Controllo di sicurezza per i Club Manager: può modificare l'iscrizione solo per atleti del proprio club
        if (securityUtils.isClubManager() && !securityUtils.isMyClub(enrollmentDTO.getClubId())) {
            log.error("Un Club Manager può aggiornare l'iscrizione solo per atleti del suo club");
            throw new UnauthorizedException("Non puoi modificare l'iscrizione di atleti per conto di altri club.");
        }

        // Controllo di sicurezza per gli Atleti: può modificare l'iscrizione solo per se stesso
        if (securityUtils.isAthlete() && (!securityUtils.getCurrentUserId().equals(enrollmentDTO.getAthleteId()) || !enrollmentDTO.getStatus().equals(EnrollmentStatus.DRAFT))) {
            log.error("Un Atleta può modificare solo la propria iscrizione e solo quando è ancora una bozza");
            throw new UnauthorizedException("Non puoi modificare l'iscrizione di altri atleti o modificare la tua iscrizione se non è in bozza.");
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Iscrizione con ID " + enrollmentDTO.getId() + " non trovata"));

        Event event = eventRepository.findById(enrollment.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento con ID " + enrollment.getEventId() + " non trovato"));

        // Valido la possibilità di modificare la registrazione.
        event.setState(EventStateFactory.getInitialState(event.getStatus()));
        event.validateRegistration(enrollmentDTO.getStatus().equals(EnrollmentStatus.DRAFT), securityUtils.isFederationManager());

        if (enrollmentDTO.getCompetitionType() != null &&
                !enrollmentDTO.getCompetitionType().equals(enrollment.getDiscipline())) {

            if (enrollmentRepository.existsByEventIdAndAthleteIdAndDiscipline(enrollmentDTO.getEventId(), enrollmentDTO.getAthleteId(), enrollmentDTO.getCompetitionType())) {
                throw new ResourceConflictException("Atleta già iscritto all'evento " + enrollmentDTO.getEventId() + " per la disciplina " + enrollmentDTO.getCompetitionType());
            }

            enrollment.setDiscipline(enrollmentDTO.getCompetitionType());
        }

        // Aggiorno i dati dell'iscrizione realmente modificabili
        enrollment.setAthleteWeight(enrollmentDTO.getAthleteWeight());
        enrollment.setAthleteHeight(enrollmentDTO.getAthleteHeight());
        enrollment.setAthleteAffiliationStatus(enrollmentDTO.getAthleteAffiliationStatus());

        this.setNewEnrollmentStatus(enrollment, enrollmentDTO.getStatus());

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        return enrollmentMapper.toDTO(updatedEnrollment);
    }


    /**
     * Implementazione del metodo per aggiornare lo stato di un'iscrizione esistente,
     * la modifica può essere fatta solo durante il periodo di apertura delle iscrizioni.
     * Controlli sui permessi:
     * - Un manager può aggiornare lo stato dell'iscrizioni solo di atleti del suo club
     * - Solo il federation manager può impostare gli stati "APPROVED" e "REJECTED"
     *
     * @param id        ID dell'iscrizione di cui si vuole aggiornare lo stato.
     * @param newStatus Nuovo stato da associare all'iscrizione
     * @return EnrollmentDTO dell'iscrizione aggiornata
     * @throws UnauthorizedException     Se il ruolo del richiedente non permette di operare sull'iscrizione indicata
     * @throws ResourceNotFoundException Se l'iscrizione non viene trovata nel database
     * @throws ActionNotAllowedException Se il ruolo del richiedente non permette la transizione di stato richiesta o
     *                                   se risulta già presente un'altra inscrizione per lo stesso evento, atleta e disciplina
     */
    @Override
    public EnrollmentDTO updateEnrollmentStatus(String id, EnrollmentStatus newStatus) {

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Iscrizione con ID " + id + " non trovata"));

        Event event = eventRepository.findById(enrollment.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento associato non trovato"));

        // Inizializzo il pattern State sull'evento
        event.setState(EventStateFactory.getInitialState(event.getStatus()));

        // Valido se l'azione è permessa nell'attuale stato dell'evento
        boolean isDraft = newStatus.equals(EnrollmentStatus.DRAFT);
        event.validateRegistration(isDraft, securityUtils.isFederationManager());

        // Un Club Manager può modificare l'iscrizione solo per atleti del proprio club
        if (securityUtils.isClubManager()) {
            if (!securityUtils.isMyClub(enrollment.getClubId())) {
                log.error("Un Club Manager può aggiornare lo stato dell'iscrizione solo per atleti del suo club");
                throw new UnauthorizedException("Non puoi modificare lo stato d'iscrizione di atleti di altri club.");
            }
        }

        this.setNewEnrollmentStatus(enrollment, newStatus);

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        return enrollmentMapper.toDTO(updatedEnrollment);
    }

    /**
     * Metodo helper privato per verificare che sia possibile impostare un nuovo stato a una determinata iscrizione:
     * - Si sfrutta il metodo dell'enumerazione EnrollmentStatus per determinare se sia permessa la transizione di stato richiesta
     * - Grazie alle securityUtils verifico il ruolo del richiedente e in base a questo verifico i permessi per eseguire la transizione di stato
     *
     * @param enrollment Oggetto di classe Enrollment a cui applicare il nuovo stato
     * @param newStatus  Enum EnrollmentStatus che rappresenta il nuovo stato da applicare
     * @throws ActionNotAllowedException Se il ruolo del richiedente non permette la transizione di stato richiesta
     */
    private void setNewEnrollmentStatus(Enrollment enrollment, EnrollmentStatus newStatus) {
        if (enrollment.getStatus() == newStatus) {
            log.info("Lo stato dell'iscrizione {} non è stato modificato", enrollment.getId());
        } else if (enrollment.getStatus().canTransitionTo(newStatus)) {
            if (securityUtils.isClubManager() && (newStatus.equals(EnrollmentStatus.REJECTED) || newStatus.equals(EnrollmentStatus.APPROVED))) {
                log.error("Un Club Manager non può approvare o rifiutare le iscrizioni");
                throw new ActionNotAllowedException("Solo i manager della federazione possono rifiutare o approvare le iscrizioni");
            } else if (securityUtils.isAthlete() && !newStatus.equals(EnrollmentStatus.DRAFT)) {
                log.error("Gli atleti possono solo impostare le iscrizioni in bozza");
                throw new ActionNotAllowedException("Puoi impostare l'iscrizione solo nello stato di bozza");
            }
            enrollment.setStatus(newStatus);
        } else {
            throw new ActionNotAllowedException("Transizione di stato non permessa.");
        }
    }

    /**
     * Metodo helper per ripristinare le iscrizioni: Porta tutte le iscrizioni non nello stato DRAFT
     */
    private void restoreEventEnrollments(String eventId) {
        List<Enrollment> enrollments = enrollmentRepository.findByEventId(eventId);

        List<Enrollment> toRestore = enrollments.stream()
                .map(e -> {
                    e.setStatus(EnrollmentStatus.DRAFT);
                    return e;
                })
                .collect(Collectors.toList());

        enrollmentRepository.saveAll(toRestore);
        log.info(" {} iscrizioni riportate in bozza per l'evento {}", toRestore.size(), eventId);
    }

    /**
     * Metodo helper per rifiutare tutte le iscrizioni se l'evento viene cancellato
     */
    private void cancelEventEnrollments(String eventId) {
        List<Enrollment> enrollments = enrollmentRepository.findByEventId(eventId);

        List<Enrollment> toCancel = enrollments.stream()
                .filter(e -> !e.getStatus().equals(EnrollmentStatus.DRAFT))
                .map(e -> {
                    e.setStatus(EnrollmentStatus.REJECTED);
                    return e;
                })
                .collect(Collectors.toList());

        enrollmentRepository.saveAll(toCancel);
    }
}
