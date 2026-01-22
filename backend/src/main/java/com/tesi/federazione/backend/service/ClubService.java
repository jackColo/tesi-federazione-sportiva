package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;

import java.util.List;

/**
 * Interfaccia tra Controller e implementazione dei service per i metodi che manipolano oggetti di tipo Club
 */
public interface ClubService {

    /**
     * Interfaccia del metodo per creare un nuovo club. Insieme al club viene creato contestualmente anche un club manager associato.
     * Lo stato d'affiliazione viene impostato di default a "SUBMITTED" affinché l'iscrizione possa essere approvata.
     * @param club CreateClubDTO oggetto contenete i dati minimi necessari alla creazione del club
     * @return ClubDTO creato correttamente.
     */
    ClubDTO createClub(CreateClubDTO club);


    /**
     * Interfaccia del metodo per recuperare un club tramite ID con controllo di sicurezza per
     * non permettere l'accesso ad altri club da parte dei club manager.
     * @param id Id del club richiesto
     * @return Club richiesto come ClubDTO
     */
    ClubDTO getClubById(String id);


    /**
     * Interfaccia del metodo per restituire l'elenco di tutti i club in un determinato stato d'affiliazione, nel caso in cui venga
     * utilizzato da un club manager o da un atleta, l'elenco viene filtrato per sicurezza, restituendo
     * solo il club a cui appartengono.
     * @param status Stato d'affiliazione per cui filtrare i club
     * @return List<ClubDTO> Elenco di tutti i club filtrati per stato d'affiliazione
     */
    List<ClubDTO> getClubsByStatus(AffiliationStatus status);


    /**
     * Interfaccia del metodo per ottenere l'elenco di tutti i club, nel caso in cui venga utilizzato da un club manager o da un atleta,
     * l'elenco viene filtrato per sicurezza, restituendo solo il club a cui appartengono
     * @return List<ClubDTO> Elenco dei club presenti con filtro di sicurezza
     */
    List<ClubDTO> getAll();


    /**
     * Interfaccia del metodo per aggiornare un club con controllo di sicurezza per non permettere l'accesso ad altri club da parte dei club manager.
     * @param club UpdatedClubDTO con i nuovi dati per il club da aggiornare
     * @return Club aggiornato come ClubDTO
     */
    ClubDTO updateClub(UpdatedClubDTO club);


    /**
     * Interfaccia del metodo per aggiornare lo stato di affiliazione di un club, per i club manager viene effettuato un controllo per
     * non permettere l'accesso ad altri club.
     * @param id Id del club di cui aggiornare lo stato di affiliazione
     * @param newStatus Nuovo stato di affiliazione per il club
     */
    void updateClubStatus(String id, AffiliationStatus newStatus);
}
