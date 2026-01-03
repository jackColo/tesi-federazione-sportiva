package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;

import java.util.List;

public interface AthleteService {
    List<AthleteDTO> getAthletesByStatusAndClubId(AffiliationStatus status, String clubId);
    void updateStatus(String id, AffiliationStatus status);
    List<AthleteDTO> getAthletesByClubId(String clubId);
    List<AthleteDTO> getAllAthletes();
}
