package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Club;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository per il salvataggio degli oggetti della classe Club nel DB Mongo.
 * Le CRUD di base non sono dichiarate in quanto definite automaticamente da Spring Data.
 */
public interface ClubRepository extends MongoRepository<Club, String> {
    /**
     * Ricerca di tutti i club che hanno uno specifico stato di affiliazione.
     *
     * @param status Stato di affiliazione per cui si effettua la ricerca
     * @return List<Club> Elenco di tutti i Club che a DB risultano nello stato specificato
     */
    List<Club> findAllByAffiliationStatus(AffiliationStatus status);
}