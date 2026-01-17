package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.mapper.UserMapper;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.AthleteService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AthleteServiceImpl implements AthleteService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AthleteServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Override
    public List<AthleteDTO> getAthletesByStatusAndClubId(AffiliationStatus status, String clubId) {
        List<Athlete> athletes = userRepository.findAllByAffiliationStatusAndClubId(status, clubId);
        return athletes.stream()
                .map(athlete -> (AthleteDTO) userMapper.toDTO(athlete))
                .toList();
    }

    @Override
    public void updateStatus(String id, AffiliationStatus newStatus) {
        Athlete athlete = (Athlete) userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Atleta con ID " + id + " non trovato"));

        if (!athlete.getAffiliationStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Transizione negata: impossibile approvare un atleta che si trova nello stato " + athlete.getAffiliationStatus()
            );
        }

        athlete.setAffiliationStatus(newStatus);

        if (newStatus.equals(AffiliationStatus.ACCEPTED)) {
            LocalDate now = LocalDate.now();
            athlete.setAffiliationDate(now);
            if (athlete.getFirstAffiliationDate() == null) {
                athlete.setFirstAffiliationDate(now);
            }
        }

        userRepository.save(athlete);
    }

    @Override
    public List<AthleteDTO> getAthletesByClubId(String clubId) {
        List<Athlete> athletes = userRepository.findAllByClubId(clubId);

        return athletes.stream()
                .map(athlete -> (AthleteDTO) userMapper.toDTO(athlete))
                .toList();
    }

    @Override
    public List<AthleteDTO> getAllAthletes() {
        List<User> athletes = userRepository.findByRole(Role.ATHLETE);
        return athletes.stream()
                .map(user -> (Athlete) user)
                .map(athlete -> (AthleteDTO) userMapper.toDTO(athlete))
                .toList();
    }
}
