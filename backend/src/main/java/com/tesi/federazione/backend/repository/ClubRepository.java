package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Club;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClubRepository extends MongoRepository<Club, String> {
    // Le CRUD di base vengono definite automaticamente da Spring Data
    List<Club> findAllByAffiliationStatus(AffiliationStatus status);
}