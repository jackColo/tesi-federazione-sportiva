package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.ClubDTO;
import com.tesi.federazione.backend.dto.ClubManagerDTO;
import com.tesi.federazione.backend.dto.CreateClubDTO;
import com.tesi.federazione.backend.dto.CreateUserDTO;
import com.tesi.federazione.backend.mapper.ClubMapper;
import com.tesi.federazione.backend.mapper.UserMapper;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.repository.ClubRepository;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.security.JwtUtils;
import com.tesi.federazione.backend.service.ClubService;
import com.tesi.federazione.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class ClubServiceImpl implements ClubService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final UserService userService;


    public ClubServiceImpl(UserRepository userRepository, ClubRepository clubRepository,  UserService userService) {
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Club createClub(CreateClubDTO dto) {
        ClubManager clubManager = (ClubManager) userService.createUser(dto.getManager());
        Club newClub = new Club();
        newClub.setName(dto.getName());
        newClub.setFiscalCode(dto.getFiscalCode());
        newClub.setLegalAddress(dto.getLegalAddress());
        newClub.setAffiliationStatus(dto.getAffiliationStatus());

        ArrayList<ClubManager> managers = new ArrayList<>();
        managers.add(clubManager);
        newClub.setManagers(managers);

        Club club = clubRepository.save(newClub);
        clubManager.setManagedClub(club);
        userRepository.save(clubManager);

        return club;
    }
}
