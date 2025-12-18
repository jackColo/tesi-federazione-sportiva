package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.user.CreateClubDTO;
import com.tesi.federazione.backend.model.Club;

import java.util.List;

public interface ClubService {
    Club createClub(CreateClubDTO club);
    List<Club> getClubsToApprove();
    void approveClub(String id) throws Exception;
}
