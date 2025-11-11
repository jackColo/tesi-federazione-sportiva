package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    // Le CRUD di base vengono definite automaticamente da Spring Data

    Optional<User> findByEmail(String email);

}