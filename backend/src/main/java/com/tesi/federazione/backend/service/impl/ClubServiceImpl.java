package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.CreateClubDTO;
import com.tesi.federazione.backend.enums.AffiliationStatus;
import com.tesi.federazione.backend.enums.Role;
import com.tesi.federazione.backend.factory.state.ClubStateFactory;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.repository.ClubRepository;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.ClubService;
import com.tesi.federazione.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Creazione contestuale di club e manager del club.
     * Transactional garantisce che vengano creati entrambi o nessuno.
     *
     *  @param dto dati per la creazione del club (manager incluso)
     */
    @Override
    @Transactional
    public Club createClub(CreateClubDTO dto) {
        // Sovrascrivo il ruolo che arriva da FE per sicurezza
        dto.getManager().setRole(Role.CLUB_MANAGER.name());

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

    @Override
    public List<Club> getClubsToApprove() {
        return clubRepository.findAllByAffiliationStatus(AffiliationStatus.SUBMITTED.name());
    }

    @Override
    public void approveClub(String id) throws Exception {
        Club club = clubRepository.findById(id).orElseThrow(() -> new Exception("Club non trovato"));

        club.setState(ClubStateFactory.getInitialState(club.getAffiliationStatus()));
        club.approve();
        clubRepository.save(club);
    }
}
