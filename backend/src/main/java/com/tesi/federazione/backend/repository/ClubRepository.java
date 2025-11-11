package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.Club;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClubRepository extends MongoRepository<Club, String> {
    // Le CRUD di base vengono definite automaticamente da Spring Data
}