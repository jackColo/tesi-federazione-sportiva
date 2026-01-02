package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Athlete;

import java.util.List;

public interface AthleteService {
    List<Athlete> getAthletesByStatusAndClubId(AffiliationStatus status, String clubId);
    void updateStatus(String id, AffiliationStatus status);
    List<Athlete> getAthletesByClubId(String clubId);
    List<Athlete> getAllAthletes();
}
