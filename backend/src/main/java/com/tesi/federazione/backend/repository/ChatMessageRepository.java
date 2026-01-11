package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository per il salvataggio degli oggetti della classe ChatMessage nel DB Mongo.
 * Le CRUD di base non sono dichiarate in quanto definite automaticamente da Spring Data.
 */
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatUserIdOrderByTimestampAsc(String clubId);
}