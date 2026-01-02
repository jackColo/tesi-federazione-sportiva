package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.repository.ClubRepository;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.ClubService;
import com.tesi.federazione.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        clubManager.setManagedClub(club.getId());
        userRepository.save(clubManager);

        return club;
    }

    @Override
    public Optional<Club> getClubById(String id) {
        return clubRepository.findById(id);
    }

    @Override
    public List<Club> getClubsByStatus(AffiliationStatus status) {
        return clubRepository.findAllByAffiliationStatus(status);
    }

    @Override
    public List<Club> getAll() {
        return clubRepository.findAll();
    }

    @Override
    public Club updateClub(UpdatedClubDTO dto) {
        Optional<Club> oldClub = getClubById(dto.getId());
        if (oldClub.isEmpty()) {
            throw new ResourceNotFoundException("Club not found");
        }

        Club newClub = new Club();
        newClub.setId(dto.getId());
        newClub.setName(dto.getName());
        newClub.setFiscalCode(dto.getFiscalCode());
        newClub.setLegalAddress(dto.getLegalAddress());
        newClub.setAffiliationStatus(oldClub.get().getAffiliationStatus());
        newClub.setManagers(oldClub.get().getManagers());
        newClub.setAthletes(oldClub.get().getAthletes());
        newClub.setAffiliationDate(oldClub.get().getAffiliationDate());
        newClub.setFirstAffiliationDate(oldClub.get().getFirstAffiliationDate());

        return clubRepository.save(newClub);
    }

    @Override
    public void updateClubStatus(String id, AffiliationStatus newStatus){
        Club club = clubRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Club con ID " + id + " non trovato"));

        if (!club.getAffiliationStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Transizione negata: impossibile portare allo stato " + newStatus + " un club che si trova nello stato " + club.getAffiliationStatus()
            );
        }

        club.setAffiliationStatus(newStatus);

        LocalDate now = LocalDate.now();
        club.setAffiliationDate(now);
        if (club.getFirstAffiliationDate() == null) {
            club.setFirstAffiliationDate(now);
        }

        clubRepository.save(club);
    }
}
