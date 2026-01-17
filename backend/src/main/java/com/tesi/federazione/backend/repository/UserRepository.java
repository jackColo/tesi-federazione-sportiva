package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per il salvataggio degli oggetti della classe User nel DB Mongo.
 * Le CRUD di base non sono dichiarate in quanto definite automaticamente da Spring Data.
 */
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Ricerca se esiste un utente (indipendentemente dal ruolo) con uno specifico indirizzo email
     * @param email Email da ricercare
     * @return Optional<User> utente ricercato se presente.
     */
    Optional<User> findByEmail(String email);

    /**
     * Ricerca di tutti gli Athlete che appartengono ad uno specifico club
     * @param clubId Id del club per cui effettuare la ricerca
     * @return List<Athlete> Elenco di tutti gli atleti appartenenti al club richiesto
     */
    List<Athlete> findAllByClubId(String clubId);
    /**
     * Ricerca di tutti gli Athlete che appartengono ad uno specifico club e hanno uno specifico stato di affiliazione
     * @param status Stato d'affiliazione da ricercare
     * @param clubId Id del club per cui effettuare la ricerca
     * @return List<Athlete> Elenco di tutti gli atleti appartenenti al club richiesto con lo stato di affiliazione indicato
     */
    List<Athlete> findAllByAffiliationStatusAndClubId(AffiliationStatus status, String clubId);

    /**
     * Ricerca di tutti gli utenti con un determinato ruolo
     * @param role Ruolo specifico da ricercare
     * @return Elenco di tutti gli utenti con ruolo specificato
     */
    List<User> findByRole(Role role);

}