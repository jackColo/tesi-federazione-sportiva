package com.tesi.federazione.backend.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.tesi.federazione.backend.model.Event;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    // Le CRUD di base vengono definite automaticamente da Spring Data

    List<Event> findByStatus(String status);
}