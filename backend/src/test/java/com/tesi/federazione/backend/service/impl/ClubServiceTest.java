package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.dto.club.CreateClubDTO;
import com.tesi.federazione.backend.dto.club.UpdatedClubDTO;
import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.mapper.ClubMapper;
import com.tesi.federazione.backend.model.Club;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.ClubRepository;
import com.tesi.federazione.backend.security.SecurityUtils;
import com.tesi.federazione.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClubServiceTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private ClubMapper clubMapper;

    @Mock
    private UserService userService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ClubServiceImpl clubService;

    @Nested
    @DisplayName("Tests per createClub()")
    class CreateClubTest {

        @Test
        @DisplayName("SUCCESSO: Crea Club, Crea Manager e collega i due")
        void success() {
            CreateClubDTO clubDto = new CreateClubDTO();
            clubDto.setName("Club");

            CreateUserDTO managerDto = new CreateUserDTO();
            managerDto.setEmail("manager@test.com");
            clubDto.setManager(managerDto);

            ClubManager savedManager = new ClubManager();
            savedManager.setId("manager-123");
            when(userService.createUserEntity(managerDto)).thenReturn(savedManager);

            Club savedClub = new Club();
            savedClub.setId("club-999");
            savedClub.setName("Super Club");
            savedClub.setManagers(new ArrayList<>());

            when(clubRepository.save(any(Club.class))).thenReturn(savedClub);

            ClubDTO expectedResult = new ClubDTO();
            expectedResult.setId("club-999");
            when(clubMapper.toDTO(savedClub)).thenReturn(expectedResult);


            ClubDTO result = clubService.createClub(clubDto);

            assertNotNull(result);
            assertEquals("club-999", result.getId());

            assertEquals(Role.CLUB_MANAGER.name(), managerDto.getRole());

            verify(userService).createUserEntity(managerDto);

            verify(clubRepository).save(any(Club.class));

            ArgumentCaptor<CreateUserDTO> userDtoCaptor = ArgumentCaptor.forClass(CreateUserDTO.class);
            verify(userService).updateUser(userDtoCaptor.capture());

            CreateUserDTO passedDto = userDtoCaptor.getValue();
            assertEquals("club-999", passedDto.getClubId());
            assertEquals("manager-123", passedDto.getId());
        }
    }

    @Nested
    @DisplayName("Tests per getClubById()")
    class GetClubByIdTest {
        @Test
        @DisplayName("SUCCESSO: restituisce il ClubDTO")
        void success() {
            String id = "clubId";
            Club club = new Club();
            club.setName("Club");
            club.setId(id);

            ClubDTO clubDto = new ClubDTO();
            clubDto.setName("Club");
            clubDto.setId(id);

            when(clubMapper.toDTO(club)).thenReturn(clubDto);
            when(securityUtils.isClubManager()).thenReturn(false);

            when(clubRepository.findById(id)).thenReturn(Optional.of(club));

            ClubDTO result = clubService.getClubById(id);

            assertNotNull(result);
            verify(clubRepository).findById(id);
            verify(clubMapper).toDTO(club);
        }

        @Test
        @DisplayName("FALLIMENTO: Club non trovato")
        void fail_NotFound() {
            String clubId = "clubId";
            when(securityUtils.isClubManager()).thenReturn(false);

            when(clubRepository.findById(clubId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                clubService.getClubById(clubId);
            });
        }

        @Test
        @DisplayName("FALLIMENTO: Fallisce verifica di sicurezza")
        void fail_ClubManagerUnauthorized() {
            String clubId = "clubId";
            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub(clubId)).thenReturn(false);

            assertThrows(ActionNotAllowedException.class, () -> {
                clubService.getClubById(clubId);
            });
        }

    }

    @Nested
    @DisplayName("Tests per getClubsByStatus()")
    class GetClubsByStatusTest {

        @Test
        @DisplayName("SUCCESSO: federation manager ottiene tutta la lista di club")
        void success_federationManager() {
            AffiliationStatus status = AffiliationStatus.ACCEPTED;
            String id = "clubId";

            Club club = new Club();
            club.setName("Club");
            club.setId(id);
            club.setAffiliationStatus(status);

            ClubDTO clubDto = new ClubDTO();
            clubDto.setName("Club");
            clubDto.setId(id);
            clubDto.setAffiliationStatus(status);

            when(clubMapper.toDTO(club)).thenReturn(clubDto);
            when(securityUtils.isClubManager()).thenReturn(false);

            when(clubRepository.findAllByAffiliationStatus(status)).thenReturn(List.of(club));

            List<ClubDTO> result = clubService.getClubsByStatus(status);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(clubRepository).findAllByAffiliationStatus(status);
            verify(clubMapper).toDTO(club);
        }

        @Test
        @DisplayName("SUCCESSO: club manager ottiene lista filtrata")
        void success_ClubManager() {
            AffiliationStatus status = AffiliationStatus.ACCEPTED;
            String managerClubId = "clubId";

            Club club = new Club();
            club.setName("Club");
            club.setId(managerClubId);
            club.setAffiliationStatus(status);

            ClubDTO clubDto = new ClubDTO();
            clubDto.setName("Club");
            clubDto.setId(managerClubId);
            clubDto.setAffiliationStatus(status);

            Club club2 = new Club();
            club2.setName("Club2");
            club2.setId("idClub2");
            club2.setAffiliationStatus(status);

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getUserClub()).thenReturn(managerClubId);
            when(clubRepository.findAllByAffiliationStatus(status)).thenReturn(List.of(club, club2));
            when(clubMapper.toDTO(club)).thenReturn(clubDto);

            List<ClubDTO> result = clubService.getClubsByStatus(status);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(clubRepository).findAllByAffiliationStatus(status);
            verify(clubMapper, times(1)).toDTO(any(Club.class));
        }

    }

    @Nested
    @DisplayName("Tests per getAll()")
    class GetAllTest {

        @Test
        @DisplayName("SUCCESSO: federation manager ottiene tutta la lista di club")
        void success_federationManager() {
            String id = "clubId";

            Club club = new Club();
            club.setName("Club");
            club.setId(id);

            ClubDTO clubDto = new ClubDTO();
            clubDto.setName("Club");
            clubDto.setId(id);

            when(clubMapper.toDTO(club)).thenReturn(clubDto);
            when(securityUtils.isClubManager()).thenReturn(false);

            when(clubRepository.findAll()).thenReturn(List.of(club));

            List<ClubDTO> result = clubService.getAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(clubRepository).findAll();
            verify(clubMapper).toDTO(club);
        }

        @Test
        @DisplayName("SUCCESSO: club manager ottiene lista filtrata")
        void success_ClubManager() {
            String managerClubId = "clubId";

            Club club = new Club();
            club.setName("Club");
            club.setId(managerClubId);

            ClubDTO clubDto = new ClubDTO();
            clubDto.setName("Club");
            clubDto.setId(managerClubId);

            Club club2 = new Club();
            club2.setName("Club2");
            club2.setId("idClub2");

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getUserClub()).thenReturn(managerClubId);
            when(clubRepository.findAll()).thenReturn(List.of(club, club2));
            when(clubMapper.toDTO(club)).thenReturn(clubDto);

            List<ClubDTO> result = clubService.getAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(clubRepository).findAll();
            verify(clubMapper, times(1)).toDTO(any(Club.class));
        }

        @Test
        @DisplayName("SUCCESSO: atleta ottiene lista filtrata")
        void success_Atleta() {
            String athleteClubId = "clubId";

            Club club = new Club();
            club.setName("Club");
            club.setId(athleteClubId);

            ClubDTO clubDto = new ClubDTO();
            clubDto.setName("Club");
            clubDto.setId(athleteClubId);

            Club club2 = new Club();
            club2.setName("Club2");
            club2.setId("idClub2");

            when(securityUtils.isClubManager()).thenReturn(false);
            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getUserClub()).thenReturn(athleteClubId);
            when(clubRepository.findAll()).thenReturn(List.of(club, club2));
            when(clubMapper.toDTO(club)).thenReturn(clubDto);

            List<ClubDTO> result = clubService.getAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(clubRepository).findAll();
            verify(clubMapper, times(1)).toDTO(any(Club.class));
        }
    }

    @Nested
    @DisplayName("Tests per updateClub()")
    class UpdateClubTest {

        @Test
        @DisplayName("SUCCESSO: Aggiorna dati richiesti mantenendo i dati non modificabili")
        void success() {
            String clubId = "club-123";

            UpdatedClubDTO updateDto = new UpdatedClubDTO();
            updateDto.setId(clubId);
            updateDto.setName("Nuovo Nome Club");
            updateDto.setLegalAddress("Nuovo Indirizzo");
            updateDto.setFiscalCode("Nuovo CF");

            Club existingClub = new Club();
            existingClub.setId(clubId);
            existingClub.setName("Vecchio Nome");
            existingClub.setAffiliationStatus(AffiliationStatus.ACCEPTED);
            existingClub.setAffiliationDate(LocalDate.of(2020, 1, 1));

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub(clubId)).thenReturn(true);
            when(clubRepository.findById(clubId)).thenReturn(Optional.of(existingClub));

            when(clubRepository.save(any(Club.class))).thenReturn(existingClub);
            when(clubMapper.toDTO(any(Club.class))).thenReturn(new ClubDTO());

            // Test del metodo
            clubService.updateClub(updateDto);

            ArgumentCaptor<Club> clubCaptor = ArgumentCaptor.forClass(Club.class);
            verify(clubRepository).save(clubCaptor.capture());

            Club savedClub = clubCaptor.getValue();

            // 1. Verifichiamo che i campi modificabili siano cambiati
            assertEquals("Nuovo Nome Club", savedClub.getName());
            assertEquals("Nuovo Indirizzo", savedClub.getLegalAddress());
            assertEquals("Nuovo CF", savedClub.getFiscalCode());

            // 2. Verifichiamo che i campi sensibili siano rimasti quelli vecchi
            assertEquals(AffiliationStatus.ACCEPTED, savedClub.getAffiliationStatus());
            assertEquals(LocalDate.of(2020, 1, 1), savedClub.getAffiliationDate());
        }

        @Test
        @DisplayName("FALLIMENTO: verifica di sicurezza non superata")
        void fail_clubManagerUnauthorized() {
            String id = "altro-clubId";
            UpdatedClubDTO dto = new UpdatedClubDTO();
            dto.setId(id);

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub(id)).thenReturn(false);

            assertThrows(ActionNotAllowedException.class, () -> clubService.updateClub(dto));

            verify(clubRepository, never()).save(any());
        }

        @Test
        @DisplayName("FALLIMENTO: Club non trovato")
        void fail_NotFound() {
            String id = "clubId";
            UpdatedClubDTO dto = new UpdatedClubDTO();
            dto.setId(id);

            when(securityUtils.isClubManager()).thenReturn(false);
            when(clubRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> clubService.updateClub(dto));
        }
    }

    @Nested
    @DisplayName("Tests per updateClubStatus()")
    class UpdateClubStatusTest {

        @Test
        @DisplayName("SUCCESSO: Transizione valida e corretta impostazione delle date")
        void success() {
            String clubId = "clubId";
            AffiliationStatus newStatus = AffiliationStatus.ACCEPTED;

            Club club = new Club();
            club.setId(clubId);
            club.setAffiliationStatus(AffiliationStatus.SUBMITTED);
            club.setFirstAffiliationDate(null);

            when(securityUtils.isClubManager()).thenReturn(false);
            when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));

            clubService.updateClubStatus(clubId, newStatus);

            ArgumentCaptor<Club> clubCaptor = ArgumentCaptor.forClass(Club.class);
            verify(clubRepository).save(clubCaptor.capture());
            Club savedClub = clubCaptor.getValue();

            assertEquals(AffiliationStatus.ACCEPTED, savedClub.getAffiliationStatus());
            assertEquals(LocalDate.now(), savedClub.getAffiliationDate());
            assertEquals(LocalDate.now(), savedClub.getFirstAffiliationDate());
        }

        @Test
        @DisplayName("FALLIMENTO: Transizione non valida")
        void fail_invalidTransition() {
            String clubId = "clubId";

            Club club = new Club();
            club.setId(clubId);
            club.setAffiliationStatus(AffiliationStatus.SUBMITTED);

            when(securityUtils.isClubManager()).thenReturn(false);
            when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));

            assertThrows(ActionNotAllowedException.class, () -> {
                clubService.updateClubStatus(clubId, AffiliationStatus.EXPIRED);
            });

            verify(clubRepository, never()).save(any());
        }

        @Test
        @DisplayName("FALLIMENTO: verifica di sicurezza non superata")
        void fail_clubManagerUnauthorized() {
            String clubId = "clubId";

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.isMyClub(clubId)).thenReturn(false);

            assertThrows(ActionNotAllowedException.class, () -> {
                clubService.updateClubStatus(clubId, AffiliationStatus.ACCEPTED);
            });

            verify(clubRepository, never()).save(any());
        }
    }
}

