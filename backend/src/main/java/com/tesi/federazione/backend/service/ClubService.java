package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.CreateClubDTO;
import com.tesi.federazione.backend.dto.ClubDTO;
import com.tesi.federazione.backend.model.Club;

import java.util.List;

public interface ClubService {
    Club createClub(CreateClubDTO club);
}
