package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.enrollment.CreateEnrollmentDTO;
import com.tesi.federazione.backend.dto.enrollment.EnrollmentDTO;
import com.tesi.federazione.backend.dto.event.CreateEventDTO;
import com.tesi.federazione.backend.dto.event.EventDTO;
import com.tesi.federazione.backend.model.enums.EnrollmentStatus;
import com.tesi.federazione.backend.model.enums.EventStatus;

import java.util.List;

/**
 * Interfaccia del servizio per la gestione degli eventi sportivi e delle relative iscrizioni.
 * Definisce i metodi per creazione e cambio stato dell'evento e i metodi per la gestione
 * delle relative iscrizioni.
 */
public interface EventService {
    /**
     * Recupera la lista di tutti gli eventi presenti a DB.
     * @return Lista completa di EventDTO.
    */
    List<EventDTO> getAllEvents();

    /**
     * Recupera il dettaglio di un singolo evento tramite ID.
     * Restituisce anche il numero corrente di iscritti.
     *
     * @param id ID dell'evento.
     * @return DTO dell'evento popolato.
     */
    EventDTO getEventById(String id);

    /**
     * Crea un nuovo evento nel sistema.
     * Operazione riservata agli amministratori della federazione.
     *
     * @param event DTO con i dati di creazione.
     * @return DTO dell'evento creato.
     */
    EventDTO createEvent(CreateEventDTO event);

    /**
     * Aggiorna i dati di un evento esistente.
     * Operazione riservata agli amministratori della federazione.
     *
     * @param eventDTO DTO con i dati dell'evento da aggiornare.
     * @return DTO dell'evento modificato.
     */
    EventDTO updateEvent(EventDTO eventDTO);

    /**
     * Modifica lo stato di un evento applicando il design pattern STATE per la
     * gestione delle transizioni da uno stato all'altro.
     * Operazione riservata agli amministratori della federazione.
     *
     * @param id ID dell'evento.
     * @param newState Nuovo stato da applicare.
     */
    void updateEventState(String id, EventStatus newState);

    /**
     * Iscrive un atleta a un evento.
     * Include controlli di sicurezza:
     * - I Club Manager possono iscrivere solo i propri atleti.
     * - Gli Atleti possono solo creare una bozza per la propria iscrizione.
     *
     * @param enrollment DTO con i dati per l'iscrizione.
     * @return DTO dell'iscrizione creata.
     */
    EnrollmentDTO enrollAthlete(CreateEnrollmentDTO enrollment);

    /**
     * Recupera le iscrizioni di un evento, con filtri opzionali per club o atleta.
     * Se l'utente è un Club Manager o un Atleta, i filtri vengono forzati per
     * limitare la visibilità ai soli dati di loro competenza.
     *
     * @param eventId ID dell'evento (Obbligatorio).
     * @param clubId ID del club (Opzionale).
     * @param athleteId ID dell'atleta (Opzionale).
     * @return Lista di iscrizioni che corrispondono ai criteri.
     */
    List<EnrollmentDTO> getEnrollmentsByEventId(String eventId, String clubId, String athleteId);

    /**
     * Recupera tutte le iscrizioni accettate di un determinato evento
     *
     * @param eventId ID dell'evento.
     * @return Lista di iscrizioni accettate relative all'evento indicato.
     */
    List<EnrollmentDTO> getApprovedEnrollmentsByEventId(String eventId);

    /**
     * Recupera una singola iscrizione per ID.
     * Controlli sui permessi:
     * - Un manager può visualizzare solo le iscrizioni di atleti del suo club
     * - Un atleta può visualizzare solo le proprie iscrizioni.
     *
     * @param id ID dell'iscrizione.
     * @return DTO dell'iscrizione.
     */
    EnrollmentDTO getEnrollment(String id);

    /**
     * Aggiorna un'iscrizione esistente, la modifica può essere fatta solo durante il periodo di apertura delle iscrizioni.
     * Controlli sui permessi:
     * - Un manager può aggiornare le iscrizioni solo di atleti del suo club
     * - Un atleta può aggiornare solo le proprie iscrizioni e solo se sono ancora in bozza.
     *
     * @param enrollment DTO con i dati aggiornati.
     * @return DTO dell'iscrizione modificata.
     */
    EnrollmentDTO updateEnrollment(EnrollmentDTO enrollment);

    /**
     * Aggiorna lo stato di un'iscrizione esistente, la modifica può essere fatta solo durante il periodo di apertura delle iscrizioni.
     * Controlli sui permessi:
     * - Un manager può aggiornare lo stato dell'iscrizioni solo di atleti del suo club
     * - Solo il federation manager può impostare gli stati "APPROVED" e "REJECTED"
     *
     * @param id ID dell'iscrizione di cui si vuole aggiornare lo stato.
     * @param newStatus Nuovo stato da associare all'iscrizione
     * @return DTO dell'iscrizione con lo stato modificato.
     */
    EnrollmentDTO updateEnrollmentStatus(String id, EnrollmentStatus newStatus);
}
