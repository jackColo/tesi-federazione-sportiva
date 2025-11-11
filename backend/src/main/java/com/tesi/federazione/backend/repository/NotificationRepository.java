package com.tesi.federazione.backend.repository;

import com.tesi.federazione.backend.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    // Le CRUD di base vengono definite automaticamente da Spring Data
}