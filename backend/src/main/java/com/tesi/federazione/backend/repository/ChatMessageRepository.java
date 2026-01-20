package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository per il salvataggio degli oggetti della classe ChatMessage nel DB Mongo.
 * Le CRUD di base non sono dichiarate in quanto definite automaticamente da Spring Data.
 */
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    /**
     * Chiamata a DB per recuperare l'elenco di tutti i messaggi inviati tra l'amministrazione e uno specifico club manager in ordine crescente
     * @param chatUserId Id del club manager per cui recuperare lo storico dei messaggi
     * @return List<ChatMessage> Elenco di tutti i messaggi inviati tra amministratori e club manager indicato
     */
    List<ChatMessage> findByChatUserIdOrderByTimestampAsc(String chatUserId);

    /**
     * Chiamata a DB paginata per recuperare l'elenco di messaggi inviati tra amministrazione e uno specifico club manager in ordine decrescente
     * @param chatUserId Id del club manager
     * @param pageable Dettagli di paginazione in formato Pagable
     * @return Elenco della pagina richiesta di messaggi inviati tra amministratori e club manager
     */
    List<ChatMessage> findByChatUserIdOrderByTimestampDesc(String chatUserId, Pageable pageable);

}