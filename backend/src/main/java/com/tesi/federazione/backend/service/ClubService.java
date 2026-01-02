package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Club;

import java.util.List;
import java.util.Optional;

public interface ClubService {
    Club createClub(CreateClubDTO club);
    Optional<Club> getClubById(String id);
    List<Club> getClubsByStatus(AffiliationStatus status);
    List<Club> getAll();
    Club updateClub(UpdatedClubDTO club);
    void updateClubStatus(String id, AffiliationStatus newStatus);
}
