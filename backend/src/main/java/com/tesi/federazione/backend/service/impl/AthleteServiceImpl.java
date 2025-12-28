package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.AthleteService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AthleteServiceImpl implements AthleteService {

    private final UserRepository userRepository;

    public AthleteServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<Athlete> getAthletesByStatusAndClubId(AffiliationStatus status, String clubId) {
        return userRepository.findAllByAffiliationStatusAndClubId(status, clubId);
    }

    @Override
    public void approveAthlete(String id) {
        Athlete athlete = (Athlete) userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Atleta con ID " + id + " non trovato"));

        if (!athlete.getAffiliationStatus().canTransitionTo(AffiliationStatus.ACCEPTED)) {
            throw new IllegalStateException(
                    "Transizione negata: impossibile approvare un atleta che si trova nello stato " + athlete.getAffiliationStatus()
            );
        }

        athlete.setAffiliationStatus(AffiliationStatus.ACCEPTED);

        LocalDate now = LocalDate.now();
        athlete.setAffiliationDate(now);
        if (athlete.getFirstAffiliationDate() == null) {
            athlete.setFirstAffiliationDate(now);
        }

        userRepository.save(athlete);
    }
}
