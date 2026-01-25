package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.exception.ResourceConflictException;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.exception.UnauthorizedException;
import com.tesi.federazione.backend.factory.user.UserCreator;
import com.tesi.federazione.backend.mapper.UserMapper;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.security.SecurityUtils;
import com.tesi.federazione.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementazione del servizio per la gestione degli utenti.
 * Gestisce creazione, lettura e aggiornamento.
 * Applica controlli di sicurezza basati sui ruoli tramite SecurityUtils.
 * Utilizza il pattern Factory per la creazione delle diverse tipologie di utenti.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;

    //Mappa utilizzata per l'applicazione del pattern Factory che associa
    //alla key del ruolo il Creator corretto
    private final Map<String, UserCreator> creators;

    /**
     * Crea un nuovo utente e restituisce il DTO corrispondente.
     *
     * @param dto DTO contenente i dati per la creazione dell'utente.
     * @return UserDTO Oggetto utente appena creato.
     */
    @Override
    public UserDTO createUser(CreateUserDTO dto) {
        User savedUser = createUserEntity(dto);
        return userMapper.toDTO(savedUser);
    }

    /**
     * Aggiorna un utente esistente verificando che esista e se l'email cambia, verifica che
     * non sia già in uso da un altro utente.
     *
     * @param dto DTO contenente i dati aggiornati.
     * @return UserDTO Oggetto utente aggiornato.
     * @throws ResourceNotFoundException Se l'utente non esiste.
     * @throws ResourceConflictException Se la nuova email è già in uso.
     * @throws ActionNotAllowedException Se il ruolo specificato non è valido.
     */
    @Override
    public UserDTO updateUser(CreateUserDTO dto) {
        User existingUser = userRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Utente con id " + dto.getId() + " non trovato"));
        String existingEmail = existingUser.getEmail();

        if (!existingEmail.equals(dto.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                log.error("La modifica dell'email {} non è consentita: è già in uso da un altro utente", dto.getEmail());
                throw new ResourceConflictException("Esiste già un utente con l'email " + dto.getEmail());
            }
        }

        User user = createEntity(dto);
        user.setId(dto.getId());
        user.setPassword(existingUser.getPassword());

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);

    }

    /**
     * Recupera un utente per email applicando delle restrizioni:
     * - ATHLETE: vede solo se stesso.
     * - CLUB_MANAGER: vede se stesso o gli atleti del proprio club.
     *
     * @param email Email dell'utente da cercare.
     * @return UserDTO DTO dell'utente trovato.
     * @throws UnauthorizedException     Se l'utente tenta di accedere ai dati di utenti per cui non ha i permessi.
     * @throws ResourceNotFoundException Se l'email non esiste.
     */
    @Override
    public UserDTO getUserByEmail(String email) {

        // Controllo di sicurezza -> un Atleta può accedere solo ai propri dati
        if (securityUtils.isAthlete() && !securityUtils.getCurrentUserEmail().equals(email)) {
            log.error("Un atleta può accedere solo ai propri dati");
            throw new UnauthorizedException("Non puoi accedere ai dati di altri utenti.");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Utente " + email + " non trovato"));

        // Controllo di sicurezza -> un Club Manager può accedere solo ai propri dati o a quelli dei propri atleti
        if (securityUtils.isClubManager()) {
            if (user instanceof ClubManager && !user.getEmail().equals(securityUtils.getCurrentUserEmail())) {
                log.error("Un club manager non può accedere ai dati di un altro club manager");
                throw new UnauthorizedException("Non puoi accedere ai dati di altri utenti.");
            } else if (user instanceof Athlete && !((Athlete) user).getClubId().equals(securityUtils.getUserClub())) {
                log.error("Un club manager non può accedere ai dati di atleti di un altro club");
                throw new UnauthorizedException("Impossibile accedere ai dati di un atleta di un altro club.");
            }
        }

        return userMapper.toDTO(user);
    }

    /**
     * Recupera un utente per id applicando delle restrizioni:
     * - ATHLETE: vede solo se stesso.
     * - CLUB_MANAGER: vede se stesso o gli atleti del proprio club.
     *
     * @param id ID dell'utente da cercare.
     * @return UserDTO DTO dell'utente trovato.
     * @throws UnauthorizedException     Se l'utente tenta di accedere ai dati di utenti per cui non ha i permessi.
     * @throws ResourceNotFoundException Se l'id non esiste.
     */
    @Override
    public UserDTO getUserById(String id) {
        // Controllo di sicurezza -> un Atleta può accedere solo ai propri dati

        if (securityUtils.isAthlete() && !securityUtils.getCurrentUserId().equals(id)) {
            log.error("Un atleta può accedere solo ai propri dati");
            throw new UnauthorizedException("Non puoi accedere ai dati di altri utenti.");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Utente con id " + id + " non trovato"));

        // Controllo di sicurezza -> un Club Manager può accedere solo ai propri dati o a quelli dei propri atleti
        if (securityUtils.isClubManager()) {
            if (user instanceof ClubManager && !user.getId().equals(securityUtils.getCurrentUserId())) {
                log.error("Un club manager non può accedere ai dati di un altro club manager");
                throw new UnauthorizedException("Non puoi accedere ai dati di altri utenti.");
            } else if (user instanceof Athlete && !((Athlete) user).getClubId().equals(securityUtils.getUserClub())) {
                log.error("Un club manager non può accedere ai dati di atleti di un altro club");
                throw new UnauthorizedException("Impossibile accedere ai dati di un atleta di un altro club.");
            }
        }
        return userMapper.toDTO(user);
    }

    /**
     * Crea e persiste l'entità User nel database.
     * Utilizza il pattern Factory per istanziare la classe corretta (es. Athlete, ClubManager) in base al ruolo.
     * La password viene cifrata prima del salvataggio.
     *
     * @param dto DTO di creazione.
     * @return User Oggetto appena creato
     * @throws UnauthorizedException     Se un Club Manager prova a creare utenti per altri club.
     * @throws ResourceConflictException Se l'email esiste già.
     * @throws ActionNotAllowedException Se il ruolo non è supportato dal Factory.
     */
    @Override
    public User createUserEntity(CreateUserDTO dto) {

        // Controllo di sicurezza -> un Club Manager può creare atleti solo per il suo club
        if (securityUtils.isClubManager() && !securityUtils.isMyClub(dto.getClubId())) {
            log.error("Un Club Manager può creare atleti solo per il suo club");
            throw new UnauthorizedException("Impossibile creare un atleta per altri club");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            log.error("Impossibile creare un utente con l'email {}: è già utilizzata da un altro utente", dto.getEmail());
            throw new ResourceConflictException("Esiste già un utente con l'email " + dto.getEmail());
        }

        User user = createEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Metodo helper privato che utilizza la mappa dei 'creators'
     * per istanziare l'oggetto User corretto in base alla stringa del ruolo presente nel DTO.
     *
     * @param dto CreateUserDTO contenente i dati necessari alla creazione dell'entity User
     * @return User entity creata tramite il creator corretto
     * @throws ActionNotAllowedException Se non esiste il creator per il ruolo indicato
     */
    private User createEntity(CreateUserDTO dto) {
        String roleKey = dto.getRole();

        UserCreator creator = creators.get(roleKey);

        if (creator == null) {
            log.error("Nessun UserCreator trovato per il ruolo: {}", roleKey);
            throw new ActionNotAllowedException("UserCreator per il ruolo " + roleKey + " non trovato");
        }

        User user = creator.createUser(dto);

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        return user;
    }
}
