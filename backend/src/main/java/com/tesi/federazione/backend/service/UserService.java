package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;

import java.util.List;

/**
 * Interfaccia per il service che gestisce le operazioni sugli utenti.
 * Gestisce la creazione, la modifica e il recupero dei dati utente con i relativi controlli di accesso.
 * Il servizio manipola oggetti di tipo User ma anche Athlete, ClubManager e FederationManager (che estendono la classe User).
 */
public interface UserService {
    /**
     * Crea un nuovo utente con hasing della password
     *
     * @param dto DTO contenente i dati per la creazione dell'utente.
     * @return UserDTO Oggetto utente appena creato.
     */
    UserDTO createUser(CreateUserDTO dto);

    /**
     * Aggiorna i dati di un utente esistente.
     * Verifica l'univocità della nuova email (se modificata) e aggiorna i campi anagrafici,
     * preservando password e ID originali.
     *
     * @param dto DTO contenente i dati aggiornati.
     * @return UserDTO Oggetto utente aggiornato.
     */
    UserDTO updateUser(CreateUserDTO dto);

    /**
     * Recupera un utente tramite indirizzo email.
     * Include controlli di sicurezza per garantire che l'utente richiedente abbia i permessi
     * per visualizzare il profilo richiesto.
     *
     * @param email Email dell'utente da cercare.
     * @return UserDTO DTO dell'utente trovato.
     */
    UserDTO getUserByEmail(String email);

    /**
     * Recupera un utente tramite il suo ID.
     * Include controlli di sicurezza per garantire che l'utente richiedente abbia i permessi
     * per visualizzare il profilo richiesto.
     *
     * @param id ID dell'utente da cercare.
     * @return UserDTO DTO dell'utente trovato.
     */
    UserDTO getUserById(String id);

    /**
     * Metodo interno al server per la creazione di uno User senza convertirlo in DTO.
     * Utilizzato da altri servizi che necessitano di creare un utente.
     *
     * @param dto DTO di creazione.
     * @return User Oggetto appena creato
     */
    User createUserEntity(CreateUserDTO dto);


    /**
     * Metodo per aggiornare la password di un utente esistente.
     * Controllo di sicurezza per consentire la modifica solo della propria password.
     *
     * @param id L'ID dell'utente di cui si vuole aggiornare la password.
     * @param newPassword vecchia password.
     * @param oldPassword nuova password.
     */
    void changeUserPassword(String id, String oldPassword, String newPassword);

    /**
     * Recupera tutti gli utenti presenti nel sistema con il ruolo indicato.
     * @param role Ruolo per cui filtrare
     * @return List<UserDTO> Lista di tutti gli atleti visibili al richiedente.
     */
    List<UserDTO> getAllByRole(Role role);
}
