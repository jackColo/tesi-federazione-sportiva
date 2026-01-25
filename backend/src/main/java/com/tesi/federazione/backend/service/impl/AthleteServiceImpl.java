package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.exception.UnauthorizedException;
import com.tesi.federazione.backend.mapper.UserMapper;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.security.SecurityUtils;
import com.tesi.federazione.backend.service.AthleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementazione del servizio per la gestione degli atleti.
 * Contiene la logica, i controlli di sicurezza (effettuati dal SecurityUtils iniettato)
 * e la gestione delle transizioni di stato dell'affiliazione.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AthleteServiceImpl implements AthleteService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;


    /**
     * Recupera una lista di atleti filtrati per stato di affiliazione e ID del club dal DB.
     *
     * @param status Stato di affilizaione richiesto
     * @param clubId ID del club a cui appartengono gli atleti.
     * @return List<AthleteDTO> Lista dei DTO degli atleti trovati.
     */
    @Override
    public List<AthleteDTO> getAthletesByStatusAndClubId(AffiliationStatus status, String clubId) {
        List<Athlete> athletes = userRepository.findAllByAffiliationStatusAndClubId(status, clubId);
        return athletes.stream()
                .map(athlete -> (AthleteDTO) userMapper.toDTO(athlete))
                .toList();
    }

    /**
     * Aggiorna lo stato di affiliazione di un atleta.
     * Include la logica per la validazione della transizione di stato e l'eventuale
     * aggiornamento delle date di tesseramento.
     * Se invocata da un club manager viene verificata l'appartenenza dell'atleta al
     * club gestito dal club manager stesso.
     *
     * @param id ID dell'atleta.
     * @param newStatus Nuovo stato da applicare.
     *
     * @throws ResourceNotFoundException Se l'atleta non esiste.
     * @throws UnauthorizedException Se un Club Manager tenta di modificare un atleta di un altro club.
     * @throws ActionNotAllowedException Se la transizione di stato non è permessa.
     */
    @Override
    public void updateStatus(String id, AffiliationStatus newStatus) {
        Athlete athlete = (Athlete) userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Atleta con ID " + id + " non trovato"));

        // Controllo di sicurezza -> un Club Manager può agire solo sui propri atleti
        if (securityUtils.isClubManager() && !securityUtils.isMyClub(athlete.getClubId())) {
            log.error("Club manager può aggiornare lo stato di affiliazione solo dei propri atleti");
            throw new UnauthorizedException("Impossibile modificare lo stato di affiliazione: atleta di un altro club.");
        }

        // Controllo che la transizione di stato sia valida
        if (!athlete.getAffiliationStatus().canTransitionTo(newStatus)) {
            log.error("Impossibile approvare un atleta che si trova nello stato {}", athlete.getAffiliationStatus());
            throw new ActionNotAllowedException(
                    "Transizione negata: impossibile approvare un atleta che si trova nello stato " + athlete.getAffiliationStatus()
            );
        }

        athlete.setAffiliationStatus(newStatus);

        // Se la transizione è verso lo stato "ACCEPTED", impostiamo la data di tesseramento odierna.
        // Se è la prima volta in assoluto, impostiamo anche la data di prima affiliazione.
        if (newStatus.equals(AffiliationStatus.ACCEPTED)) {
            LocalDate now = LocalDate.now();
            athlete.setAffiliationDate(now);
            if (athlete.getFirstAffiliationDate() == null) {
                athlete.setFirstAffiliationDate(now);
            }
        }

        userRepository.save(athlete);
    }


    /**
     * Recupera tutti gli atleti appartenenti a uno specifico club.
     *
     * @param clubId ID del club.
     * @return List<AthleteDTO> Lista completa degli atleti del club richiesto.
     *
     * @throws UnauthorizedException Se un Club Manager tenta di recuperare gli atleti di un altro club.
     */
    @Override
    public List<AthleteDTO> getAthletesByClubId(String clubId) {

        if (securityUtils.isClubManager() && !securityUtils.isMyClub(clubId)) {
            log.error("Club manager leggere i dati solo degli atleti del proprio club.");
            throw new UnauthorizedException("Impossibile recuperare i dati degli atleti di un altro club.");
        }

        List<Athlete> athletes = userRepository.findAllByClubId(clubId);
        return athletes.stream()
                .map(athlete -> (AthleteDTO) userMapper.toDTO(athlete))
                .toList();
    }

    /**
     * Recupera tutti gli atleti presenti nel sistema.
     * Se invocato da un Club Manager, dovrebbe ritornare solo gli atleti del proprio club.
     *
     * @return List<AthleteDTO> Lista di tutti gli atleti visibili al richiedente.
     */
    @Override
    public List<AthleteDTO> getAllAthletes() {

        List<User> athletes = userRepository.findByRole(Role.ATHLETE);

        // Se è un club manager filtro i dati per restituire solo gli atleti del suo club
        if (securityUtils.isClubManager()) {
            String myClubId = securityUtils.getUserClub();
            log.warn("L'utente è un Club Manager, i risultati verranno filtrati con l'id del suo club {}", myClubId);
            return athletes.stream()
                    .map(user -> (Athlete) user)
                    .filter(athlete -> athlete.getClubId().equals(myClubId))
                    .map(athlete -> (AthleteDTO) userMapper.toDTO(athlete))
                    .toList();
        }

        // Se è un federation manager restituisco tutto
        return athletes.stream()
                .map(user -> (Athlete) user)
                .map(athlete -> (AthleteDTO) userMapper.toDTO(athlete))
                .toList();
    }
}
