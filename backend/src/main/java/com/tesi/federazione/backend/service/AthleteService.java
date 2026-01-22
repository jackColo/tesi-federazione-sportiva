package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;

import java.util.List;

/**
 * Interfaccia per il service che manipola oggetti di classe ATHLETE.
 * Gestisce il recupero dei dati e l'aggiornamento degli stati d'affiliazione.
 */
public interface AthleteService {

    /**
     * Interfaccia del metodo che recupera una lista di atleti filtrati per stato di affiliazione e ID del club.
     *
     * @param status Stato di affilizaione richiesto
     * @param clubId ID del club a cui appartengono gli atleti.
     * @return List<AthleteDTO> Lista dei DTO degli atleti trovati.
     */
    List<AthleteDTO> getAthletesByStatusAndClubId(AffiliationStatus status, String clubId);

    /**
     * Aggiorna lo stato di affiliazione di un atleta.
     * Include la logica per la validazione della transizione di stato e l'eventuale
     * aggiornamento delle date di tesseramento.
     * Se invocata da un club manager viene verificata l'appartenenza dell'atleta al
     * club gestito dal club manager stesso.
     *
     * @param id ID dell'atleta.
     * @param status Nuovo stato da applicare.
     */
    void updateStatus(String id, AffiliationStatus status);

    /**
     * Recupera tutti gli atleti appartenenti a uno specifico club.
     *
     * @param clubId ID del club.
     * @return List<AthleteDTO> Lista completa degli atleti del club richiesto.
     */
    List<AthleteDTO> getAthletesByClubId(String clubId);

    /**
     * Recupera tutti gli atleti presenti nel sistema.
     * Se invocato da un Club Manager, dovrebbe ritornare solo gli atleti del proprio club.
     *
     * @return List<AthleteDTO> Lista di tutti gli atleti visibili al richiedente.
     */
    List<AthleteDTO> getAllAthletes();
}
