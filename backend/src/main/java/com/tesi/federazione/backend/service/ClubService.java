package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Club;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface ClubService {
    ClubDTO createClub(CreateClubDTO club);
    ClubDTO getClubById(String id);
    List<ClubDTO> getClubsByStatus(AffiliationStatus status);
    List<ClubDTO> getAll();
    ClubDTO updateClub(UpdatedClubDTO club);
    void updateClubStatus(String id, AffiliationStatus newStatus);
}
