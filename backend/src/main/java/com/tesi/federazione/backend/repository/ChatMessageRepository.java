package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    // Le CRUD di base vengono definite automaticamente da Spring Data
}