package com.tesi.federazione.backend.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.tesi.federazione.backend.model.Event;

import java.util.List;

/**
 * Repository per il salvataggio degli oggetti della classe Event nel DB Mongo.
 * Le CRUD di base non sono dichiarate in quanto definite automaticamente da Spring Data.
 */
public interface EventRepository extends MongoRepository<Event, String> {
    /**
     * Ricerca di tutti gli eventi che hanno uno specifico stato.
     *
     * @param status Stato per cui si effettua la ricerca
     * @return List<Event> Elenco di tutti gli eventi che a DB risultano nello stato richiesto
     */
    List<Event> findByStatus(String status);
}