package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.mapper.ClubMapper;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.ClubRepository;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.security.SecurityUtils;
import com.tesi.federazione.backend.service.ClubService;
import com.tesi.federazione.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione del servizio ClubService, necessario alla manipolazione a DB degli oggetti di tipo Club
 */
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    private final UserService userService;

    private final ClubMapper clubMapper;

    private final SecurityUtils securityUtils;

    /**
     * Metodo per creare un nuovo club. Insieme al club viene creato contestualmente anche un club manager associato.
     * Lo stato d'affiliazione viene impostato di default a "SUBMITTED" afficnhè l'iscrizione possa essere approvata.
     * @param dto CreateClubDTO oggetto contenete i dati minimi necessari alla creazione del club
     * @return ClubDTO creato correttamente.
     */
    @Override
    @Transactional
    public ClubDTO createClub(CreateClubDTO dto) {
        // Sovrascrivo il ruolo che arriva da FE per sicurezza
        dto.getManager().setRole(Role.CLUB_MANAGER.name());

        // Creo prima il club manager, per poterlo associare al club
        ClubManager clubManager = (ClubManager) userService.createUserEntity(dto.getManager());

        // Creo il nuovo club
        Club newClub = new Club();
        newClub.setName(dto.getName());
        newClub.setFiscalCode(dto.getFiscalCode());
        newClub.setLegalAddress(dto.getLegalAddress());
        newClub.setAffiliationStatus(dto.getAffiliationStatus());
        ArrayList<ClubManager> managers = new ArrayList<>();
        managers.add(clubManager);
        newClub.setManagers(managers);
        Club club = clubRepository.save(newClub);

        // Aggiorno il clubManager creato associandogli l'id del club
        clubManager.setManagedClub(club.getId());
        userRepository.save(clubManager);

        return clubMapper.toDTO(club);
    }

    /**
     * Metodo per recuperare un club tramite ID con controllo di sicurezza per
     * non permettere l'accesso ad altri club da parte dei club manager.
     * @param id Id del club richiesto
     * @return Club richiesto come ClubDTO
     */
    @Override
    public ClubDTO getClubById(String id) {
        if (securityUtils.isClubManager() && !securityUtils.isMyClub(id)) {
            throw new ActionNotAllowedException("Accesso al club richiesto non autorizzato");
        }
        Club club = findClubEntity(id);
        return clubMapper.toDTO(club);
    }

    /**
     * Metodo per restituire l'elenco di tutti i club in un determinato stato d'affiliazione, nel caso in cui venga
     * utilizzato da un club manager o da un atleta, l'elenco viene filtrato per sicurezza, restituiendo
     * solo il club a cui appartengono.
     * @param status Stato d'affiliazione per cui filtrare i club
     * @return List<ClubDTO> Elenco di tutti i club filtrati per stato d'affiliazione
     */
    @Override
    public List<ClubDTO> getClubsByStatus(AffiliationStatus status) {
        List<Club> clubs = clubRepository.findAllByAffiliationStatus(status);
        if (securityUtils.isClubManager()) {
            String userClub = securityUtils.getUserClub();
            return clubs.stream().filter(club -> club.getId().equals(userClub)).map(clubMapper::toDTO).toList();
        }
        return clubs.stream().map(clubMapper::toDTO).toList();
    }

    /**
     * Metodo per ottenere l'elenco di tutti i club, nel caso in cui venga utilizzato da un club manager o da un atleta,
     * l'elenco viene filtrato per sicurezza, restituiendo solo il club a cui appartengono
     * @return List<ClubDTO> Elenco dei club presenti con filtro di sicurezza
     */
    @Override
    public List<ClubDTO> getAll() {
        List<Club> clubs = clubRepository.findAll();
        if (securityUtils.isClubManager() || securityUtils.isAthlete()) {
            String userClub = securityUtils.getUserClub();
            return clubs.stream().filter(club -> club.getId().equals(userClub)).map(clubMapper::toDTO).toList();
        }
        return clubs.stream().map(clubMapper::toDTO).toList();
    }

    /**
     * Metodo per aggiornare un club con controllo di sicurezza per non permettere l'accesso ad altri club da parte dei club manager.
     * @param dto UpdatedClubDTO con i nuovi dati per il club da aggiornare
     * @return Club aggiornato come ClubDTO
     */
    @Override
    public ClubDTO updateClub(UpdatedClubDTO dto) {
        if (securityUtils.isClubManager() && !securityUtils.isMyClub(dto.getId())) {
            throw new ActionNotAllowedException("Accesso al club richiesto non autorizzato");
        }

        Club oldClub = findClubEntity(dto.getId());

        Club newClub = new Club();
        newClub.setId(dto.getId());
        newClub.setName(dto.getName());
        newClub.setFiscalCode(dto.getFiscalCode());
        newClub.setLegalAddress(dto.getLegalAddress());

        newClub.setAffiliationStatus(oldClub.getAffiliationStatus());
        newClub.setManagers(oldClub.getManagers());
        newClub.setAthletes(oldClub.getAthletes());
        newClub.setAffiliationDate(oldClub.getAffiliationDate());
        newClub.setFirstAffiliationDate(oldClub.getFirstAffiliationDate());

        Club club = clubRepository.save(newClub);
        return clubMapper.toDTO(club);
    }

    /**
     * Metodo per aggiornare lo stato di affiliazione di un club, per i club manager viene effettuato un controllo per
     * non permettere l'accesso ad altri club.
     * @param id Id del club di cui aggionrare lo stato di affiliazione
     * @param newStatus Nuovo stato di affiliazione per il club
     */
    @Override
    public void updateClubStatus(String id, AffiliationStatus newStatus){

        // Verifica dei permessi di accesso al club
        if (securityUtils.isClubManager() && !securityUtils.isMyClub(id)) {
            throw new ActionNotAllowedException("Impossibile modificare lo stato di un altro club!");
        }

        Club club = findClubEntity(id);

        // Verifica della transizione di stato richiesta
        if (!club.getAffiliationStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Transizione negata: impossibile portare allo stato " + newStatus + " un club che si trova nello stato " + club.getAffiliationStatus()
            );
        }

        club.setAffiliationStatus(newStatus);

        if (newStatus.equals(AffiliationStatus.ACCEPTED)) {
            LocalDate now = LocalDate.now();
            club.setAffiliationDate(now);
            if (club.getFirstAffiliationDate() == null) {
                club.setFirstAffiliationDate(now);
            }
        }

        clubRepository.save(club);
    }

    /**
     * Metodo utility per recuperare un club tramite id un club come Entity e non come DTO
     * @param id Id del club da ricercare
     * @return Club richiesto in formato Club
     */
    private Club findClubEntity(String id) {
        return clubRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Club con ID " + id + " non trovato"));
    }
}
