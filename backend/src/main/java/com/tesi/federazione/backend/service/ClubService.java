package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;

import java.util.List;

public interface ClubService {
    ClubDTO createClub(CreateClubDTO club);
    ClubDTO getClubById(String id);
    List<ClubDTO> getClubsByStatus(AffiliationStatus status);
    List<ClubDTO> getAll();
    ClubDTO updateClub(UpdatedClubDTO club);
    void updateClubStatus(String id, AffiliationStatus newStatus);

    Club findClubEntity(String id);
}
