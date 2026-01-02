package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    // Le CRUD di base vengono definite automaticamente da Spring Data

    Optional<User> findByEmail(String email);
    List<Athlete> findAllByAffiliationStatusAndClubId(AffiliationStatus status, String clubId);
    List<Athlete> findAllByClubId(String clubId);
    List<Athlete> findByRole(Role role);

}