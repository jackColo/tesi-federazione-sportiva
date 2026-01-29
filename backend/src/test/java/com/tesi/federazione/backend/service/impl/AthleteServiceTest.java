package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.AthleteDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.exception.UnauthorizedException;
import com.tesi.federazione.backend.mapper.UserMapper;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AthleteServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    SecurityUtils securityUtils;

    @InjectMocks
    AthleteServiceImpl athleteService;

    @Nested
    @DisplayName("Tests per: getAthletesByStatusAndClubId()")
    class GetAthletesByStatusAndClubIdTest {

        @Test
        @DisplayName("SUCCESSO: Restituisce lista di DTO se trovati atleti")
        public void success_athleteList() {
            AffiliationStatus status = AffiliationStatus.SUBMITTED;
            String clubId = "clubId";

            Athlete athlete = new Athlete();
            athlete.setClubId(clubId);
            athlete.setAffiliationStatus(status);
            athlete.setId("1234");

            AthleteDTO athleteDTO = new AthleteDTO();
            athleteDTO.setId(athlete.getId());

            when(userRepository.findAllByAffiliationStatusAndClubId(status,  clubId)).thenReturn(List.of(athlete));
            when(userMapper.toDTO(athlete)).thenReturn(athleteDTO);

            List<AthleteDTO> athletes = athleteService.getAthletesByStatusAndClubId(status, clubId);

            assertNotNull(athletes);
            assertEquals(1, athletes.size());
            assertEquals(athlete.getId(), athletes.get(0).getId());

            verify(userRepository).findAllByAffiliationStatusAndClubId(status, clubId);
            verify(userMapper).toDTO(athlete);
        }

        @Test
        @DisplayName("SUCCESSO: Restituisce lista vuota se non trovati atleti")
        public void success_empty() {
            AffiliationStatus status = AffiliationStatus.SUBMITTED;
            String clubId = "clubId";

            when(userRepository.findAllByAffiliationStatusAndClubId(status,  clubId)).thenReturn(Collections.emptyList());

            List<AthleteDTO> athletes = athleteService.getAthletesByStatusAndClubId(status, clubId);

            assertNotNull(athletes);
            assertEquals(0, athletes.size());

            verify(userMapper, never()).toDTO(any());
        }
    }

    @Nested
    @DisplayName("Tests per: updateStatus()")
    class updateStatusTest {

        @Test
        @DisplayName("SUCCESSO: Data rinnovo aggiornata")
        public void success_renewDateSet() {
            AffiliationStatus newStatus = AffiliationStatus.ACCEPTED;
            LocalDate oldFirstDate = LocalDate.of(2020, 1, 1);
            String athleteId = "athleteId";
            String clubId = "clubId";

            Athlete athlete = new Athlete();
            athlete.setClubId(clubId);
            athlete.setAffiliationStatus(AffiliationStatus.SUBMITTED);
            athlete.setId(athleteId);
            athlete.setFirstAffiliationDate(oldFirstDate);

            when(userRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
            when(securityUtils.isClubManager()).thenReturn(false);

            athleteService.updateStatus(athleteId, newStatus);

            verify(userRepository).findById(athleteId);
            ArgumentCaptor<Athlete> athleteCaptor = ArgumentCaptor.forClass(Athlete.class);
            verify(userRepository).save(athleteCaptor.capture());

            Athlete savedAthlete = athleteCaptor.getValue();

            assertEquals(LocalDate.now(), savedAthlete.getAffiliationDate());
            assertEquals(oldFirstDate, savedAthlete.getFirstAffiliationDate());
        }

        @Test
        @DisplayName("SUCCESSO: Data prima affiliazione inserita")
        public void success_firstAffiliationDateSet() {
            AffiliationStatus newStatus = AffiliationStatus.ACCEPTED;
            String athleteId = "athleteId";
            String clubId = "clubId";

            Athlete athlete = new Athlete();
            athlete.setClubId(clubId);
            athlete.setAffiliationStatus(AffiliationStatus.SUBMITTED);
            athlete.setId(athleteId);

            when(userRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
            when(securityUtils.isClubManager()).thenReturn(false);

            athleteService.updateStatus(athleteId, newStatus);

            verify(userRepository).findById(athleteId);
            ArgumentCaptor<Athlete> athleteCaptor = ArgumentCaptor.forClass(Athlete.class);
            verify(userRepository).save(athleteCaptor.capture());

            Athlete savedAthlete = athleteCaptor.getValue();

            assertEquals(AffiliationStatus.ACCEPTED, savedAthlete.getAffiliationStatus());
            assertNotNull(savedAthlete.getAffiliationDate());
            assertNotNull(savedAthlete.getFirstAffiliationDate());
            assertEquals(LocalDate.now(), savedAthlete.getAffiliationDate());
        }

        @Test
        @DisplayName("FALLIMENTO: Verifica di sicurezza fallisce")
        public void fail_clubManagerUnauthorized() {
            String athleteId = "athleteId";
            AffiliationStatus newStatus = AffiliationStatus.ACCEPTED;
            String clubId = "clubId";

            Athlete athlete = new Athlete();
            athlete.setClubId(clubId);
            athlete.setAffiliationStatus(AffiliationStatus.SUBMITTED);
            athlete.setId(athleteId);

            when(userRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub(clubId)).thenReturn(false);

            assertThrows(UnauthorizedException.class, () -> athleteService.updateStatus(athleteId, newStatus));

            verify(userRepository).findById(athleteId);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("FALLIMENTO: Transizione non valida")
        public void fail_invalidStatusTransition() {
            String athleteId = "athleteId";
            AffiliationStatus newStatus = AffiliationStatus.ACCEPTED;
            String clubId = "clubId";

            Athlete athlete = new Athlete();
            athlete.setClubId(clubId);
            athlete.setAffiliationStatus(AffiliationStatus.REJECTED);
            athlete.setId(athleteId);

            when(userRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
            when(securityUtils.isClubManager()).thenReturn(false);

            assertThrows(ActionNotAllowedException.class, () -> athleteService.updateStatus(athleteId, newStatus));

            verify(userRepository).findById(athleteId);
            verify(userRepository, never()).save(any());
        }


        @Test
        @DisplayName("FALLIMENTO: Utente non trovato")
        public void fail_userNotFound() {
            String athleteId = "athleteId";
            AffiliationStatus newStatus = AffiliationStatus.ACCEPTED;

            when(userRepository.findById(athleteId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> athleteService.updateStatus(athleteId, newStatus));

            verify(userRepository).findById(athleteId);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests per: getAthletesByClubId()")
    class getAthletesByClubIdTest {

        @Test
        @DisplayName("SUCCESSO: Restituisce atleti del club richiesto")
        public void success() {
            String clubId = "clubId";

            Athlete athlete = new Athlete();
            athlete.setId("2345");
            athlete.setClubId(clubId);

            AthleteDTO athleteDto = new AthleteDTO();
            athleteDto.setId(athlete.getId());
            athlete.setClubId(clubId);

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub(clubId)).thenReturn(true);

            when(userMapper.toDTO(athlete)).thenReturn(athleteDto);
            when(userRepository.findAllByClubId(clubId)).thenReturn(List.of(athlete));

            List<AthleteDTO> result = athleteService.getAthletesByClubId(clubId);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(userRepository).findAllByClubId(clubId);
        }

        @Test
        @DisplayName("SUCCESSO: Restituisce lista vuota se non trovo atleti")
        public void success_empty() {
            String clubId = "clubId";

            when(securityUtils.isClubManager()).thenReturn(false);
            when(userRepository.findAllByClubId(clubId)).thenReturn(Collections.emptyList());

            List<AthleteDTO> result = athleteService.getAthletesByClubId(clubId);

            assertTrue(result.isEmpty());
            verify(userMapper, never()).toDTO(any());
        }

        @Test
        @DisplayName("FALLIMENTO: Verifica di sicurezza fallisce")
        public void fail_clubManagerUnauthorized() {
            String clubId = "clubId";

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub(clubId)).thenReturn(false);

            assertThrows(UnauthorizedException.class, () -> athleteService.getAthletesByClubId(clubId));

            verify(userRepository, never()).findAllByClubId(any());
        }

    }

    @Nested
    @DisplayName("Tests per: getAllAthletes()")
    class getAllAthletesTest {

        @Test
        @DisplayName("SUCCESSO: Restituisce tutti gli atleti se sono un federation manager")
        public void success_federationManager() {

            when(securityUtils.isClubManager()).thenReturn(false);

            Athlete myAthlete = new Athlete();
            myAthlete.setId("2345");
            myAthlete.setClubId("clubId");

            Athlete otherAthlete = new Athlete();
            otherAthlete.setId("2222");
            otherAthlete.setClubId("OTHER_CLUB");

            when(userRepository.findByRole(Role.ATHLETE)).thenReturn(List.of(myAthlete, otherAthlete));
            when(userMapper.toDTO(any(Athlete.class))).thenReturn(new AthleteDTO());

            List<AthleteDTO> result = athleteService.getAllAthletes();

            assertEquals(2, result.size());
            verify(userMapper, times(2)).toDTO(any(Athlete.class));
        }

        @Test
        @DisplayName("SUCCESSO: Restituisce solo gli atleti del mio club se sono un club manager")
        public void success_clubManager() {
            String myClubId = "MY_CLUB";

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getUserClub()).thenReturn(myClubId);

            Athlete myAthlete = new Athlete();
            myAthlete.setId("2345");
            myAthlete.setClubId(myClubId);

            Athlete otherAthlete = new Athlete();
            otherAthlete.setId("2222");
            otherAthlete.setClubId("OTHER_CLUB");

            when(userRepository.findByRole(Role.ATHLETE)).thenReturn(List.of(myAthlete, otherAthlete));
            when(userMapper.toDTO(any(Athlete.class))).thenReturn(new AthleteDTO());

            List<AthleteDTO> result = athleteService.getAllAthletes();

            assertEquals(1, result.size());
            verify(userMapper, times(1)).toDTO(any(Athlete.class));
        }

        @Test
        @DisplayName("SUCCESSO: Restituisce lista vuota se non ci sono atleti nel sistema")
        void test_empty() {
            when(userRepository.findByRole(Role.ATHLETE)).thenReturn(Collections.emptyList());
            List<AthleteDTO> result = athleteService.getAllAthletes();

            assertTrue(result.isEmpty());
        }
    }
}
