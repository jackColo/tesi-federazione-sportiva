package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository per il salvataggio degli oggetti della classe ChatSession nel DB Mongo.
 * Le CRUD di base non sono dichiarate in quanto definite automaticamente da Spring Data.
 */
public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {

    /**
     * Ricerca a DB se esiste una sessione attiva per uno specifico club manager, in caso positivo la restituisce.
     *
     * @param clubManagerId Id del club manager per cui verificare l'esistenza di una sessione attiva
     * @return ChatSession Informazioni sulla sessione attiva.
     */
    Optional<ChatSession> findByClubManagerIdAndActiveTrue(String clubManagerId);

    /**
     * Ricerca a DB se esiste una sessione attiva per uno specifico amministratore, in caso positivo la restituisce.
     *
     * @param adminId Id dell'admin per cui verificare l'esistenza di una sessione attiva
     * @return ChatSession Informazioni sulla sessione attiva.
     */
    Optional<ChatSession> findByAdminIdAndActiveTrue(String adminId);
}
