package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.mapper.ClubMapper;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.ClubRepository;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.ClubService;
import com.tesi.federazione.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    private final UserService userService;

    private final ClubMapper clubMapper;

    @Override
    @Transactional
    public ClubDTO createClub(CreateClubDTO dto) {
        // Sovrascrivo il ruolo che arriva da FE per sicurezza
        dto.getManager().setRole(Role.CLUB_MANAGER.name());

        ClubManager clubManager = (ClubManager) userService.createUserEntity(dto.getManager());
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

        return clubMapper.toDTO(club);
    }

    @Override
    public Club findClubEntity(String id) {
        return clubRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Club con ID " + id + " non trovato"));
    }

    @Override
    public ClubDTO getClubById(String id) {
        Club club = findClubEntity(id);
        return clubMapper.toDTO(club);
    }

    @Override
    public List<ClubDTO> getClubsByStatus(AffiliationStatus status) {
        List<Club> clubs = clubRepository.findAllByAffiliationStatus(status);
        return clubs.stream().map(clubMapper::toDTO).toList();
    }

    @Override
    public List<ClubDTO> getAll() {
        List<Club> clubs = clubRepository.findAll();
        return clubs.stream().map(clubMapper::toDTO).toList();
    }

    @Override
    public ClubDTO updateClub(UpdatedClubDTO dto) {
        Club oldClub = findClubEntity(dto.getId());

        Club newClub = new Club();
        newClub.setId(dto.getId());
        newClub.setName(dto.getName());
        newClub.setFiscalCode(dto.getFiscalCode());
        newClub.setLegalAddress(dto.getLegalAddress());

        newClub.setAffiliationStatus(oldClub.getAffiliationStatus());
        newClub.setManagers(oldClub.getManagers());
        newClub.setAthletes(oldClub.getAthletes());
        newClub.setAffiliationDate(oldClub.getAffiliationDate());
        newClub.setFirstAffiliationDate(oldClub.getFirstAffiliationDate());

        Club club = clubRepository.save(newClub);
        return clubMapper.toDTO(club);
    }

    @Override
    public void updateClubStatus(String id, AffiliationStatus newStatus){
        Club club = findClubEntity(id);

        if (!club.getAffiliationStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Transizione negata: impossibile portare allo stato " + newStatus + " un club che si trova nello stato " + club.getAffiliationStatus()
            );
        }

        club.setAffiliationStatus(newStatus);

        if (newStatus.equals(AffiliationStatus.ACCEPTED)) {
            LocalDate now = LocalDate.now();
            club.setAffiliationDate(now);
            if (club.getFirstAffiliationDate() == null) {
                club.setFirstAffiliationDate(now);
            }
        }

        clubRepository.save(club);
    }
}
