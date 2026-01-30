package com.tesi.federazione.backend.service.impl.chat;

import com.tesi.federazione.backend.exception.ResourceConflictException;
import com.tesi.federazione.backend.model.ChatSession;
import com.tesi.federazione.backend.repository.ChatSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatAssignmentManagerTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @InjectMocks
    private ChatAssignmentManager chatAssignmentManager;

    @Nested
    @DisplayName("Tests per: assignChat()")
    class AssignChatTest {

        @Test
        @DisplayName("SUCCESSO: assegnazione corretta (admin libero e chat non presa in carico)")
        void success() {
            String adminId = "adminId";
            String clubManagerId = "clubId";

            when(chatSessionRepository.findByAdminIdAndActiveTrue(adminId)).thenReturn(Optional.empty());
            when(chatSessionRepository.findByClubManagerIdAndActiveTrue(clubManagerId)).thenReturn(Optional.empty());

            chatAssignmentManager.assignChat(clubManagerId, adminId);

            verify(chatSessionRepository).save(any(ChatSession.class));
        }

        @Test
        @DisplayName("FALLIMENTO: Admin già occupato")
        void fail_AdminBusy() {
            String adminId = "adminId";
            String clubManagerId = "clubId";

            when(chatSessionRepository.findByAdminIdAndActiveTrue(adminId))
                    .thenReturn(Optional.of(new ChatSession()));

            assertThrows(ResourceConflictException.class, () ->
                    chatAssignmentManager.assignChat(clubManagerId, adminId)
            );

            verify(chatSessionRepository, never()).findByClubManagerIdAndActiveTrue(anyString());
            verify(chatSessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("FALLIMENTO: Chat già presa in carico da un altro admin")
        void fail_chatTaken() {
            String adminId = "adminId";
            String clubManagerId = "clubId";

            when(chatSessionRepository.findByAdminIdAndActiveTrue(adminId)).thenReturn(Optional.empty());
            when(chatSessionRepository.findByClubManagerIdAndActiveTrue(clubManagerId))
                    .thenReturn(Optional.of(new ChatSession()));

            assertThrows(ResourceConflictException.class, () ->
                    chatAssignmentManager.assignChat(clubManagerId, adminId)
            );

            verify(chatSessionRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("SUCCESSO: Test per releaseChat()")
    void releaseChatTest_success() {
        String clubManagerId = "clubId";
        ChatSession session = new ChatSession();
        session.setActive(true);

        when(chatSessionRepository.findByClubManagerIdAndActiveTrue(clubManagerId))
                .thenReturn(Optional.of(session));

        chatAssignmentManager.releaseChat(clubManagerId);

        assertFalse(session.isActive());
        verify(chatSessionRepository).save(session);
    }

    @Test
    @DisplayName("SUCCESSO: Test per getCurrentAdminForClubManager()")
    void GetCurrentAdminForClubManagerTest_success() {
        String clubManagerId = "clubId";
        ChatSession session = new ChatSession();
        session.setAdminId("adminId");

        when(chatSessionRepository.findByClubManagerIdAndActiveTrue(clubManagerId))
                .thenReturn(Optional.of(session));

        String result = chatAssignmentManager.getCurrentAdminForClubManager(clubManagerId);

        assertEquals("adminId", result);
    }


    @Nested
    @DisplayName("Tests per: assignChat() - Test sulla gestione della concorrenza")
    class AssignChat_concurrencyTests {

        @Test
        @DisplayName("Concorrenza: 2 Admin contesi sulla stessa chat")
        void sameChat_TwoAdmins_test() throws InterruptedException {
            String clubId = "clubId";
            String admin1 = "admin1";
            String admin2 = "admin2";

            // AtomicBoolean serve per simulare lo stato "in memoria" durante il test altrimenti entrambi i
            // thread che creiamo in seguito vedrebbero sempre isChatTaker = false e non riusciremmo a testare
            AtomicBoolean isChatTaken = new AtomicBoolean(false);

            when(chatSessionRepository.findByClubManagerIdAndActiveTrue(clubId)).thenAnswer(inv -> {
                if (isChatTaken.get()) {
                    return Optional.of(new ChatSession());
                }
                return Optional.empty();
            });

            when(chatSessionRepository.save(any())).thenAnswer(inv -> {
                isChatTaken.set(true);
                // Imposto una latenza nel salvataggio per mantenere il lock sul primo thread e favorire
                // l'effettiva verifica dell'efficacia nella gestione della concorrenza
                Thread.sleep(200);
                return inv.getArgument(0);
            });

            when(chatSessionRepository.findByAdminIdAndActiveTrue(anyString())).thenReturn(Optional.empty());

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            Thread t1 = new Thread(() -> {
                try {
                    chatAssignmentManager.assignChat(clubId, admin1);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            Thread t2 = new Thread(() -> {
                try {
                    chatAssignmentManager.assignChat(clubId, admin2);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            t1.start();
            t2.start();

            //  uso .join() per fare in modo che il test attenda che i thread terminino
            t1.join();
            t2.join();

            assertEquals(1, successCount.get());
            assertEquals(1, failCount.get());
            verify(chatSessionRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("Concorrenza: Stesso Admin prova a prendere 2 chat diverse")
        void twoChats_sameAdmin_test() throws InterruptedException {
            String adminId = "adminId";
            String userChatId1 = "clubManagerId1";
            String userChatId2 = "clubManagerId2";

            // AtomicBoolean serve per simulare lo stato "in memoria" durante il test altrimenti entrambi i
            // thread che creiamo in seguito vedrebbero sempre isChatTaker = false e non riusciremmo a testare
            AtomicBoolean isAdminBusy = new AtomicBoolean(false);

            when(chatSessionRepository.findByAdminIdAndActiveTrue(adminId)).thenAnswer(inv -> {
                if (isAdminBusy.get()) {
                    return Optional.of(new ChatSession());
                }
                return Optional.empty();
            });

            when(chatSessionRepository.save(any())).thenAnswer(inv -> {
                isAdminBusy.set(true);
                // Imposto una latenza nel salvataggio per mantenere il lock sul primo thread e favorire
                // l'effettiva verifica dell'efficacia nella gestione della concorrenza
                Thread.sleep(200);
                return inv.getArgument(0);
            });

            when(chatSessionRepository.findByClubManagerIdAndActiveTrue(anyString())).thenReturn(Optional.empty());

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            Thread t1 = new Thread(() -> {
                try {
                    chatAssignmentManager.assignChat(userChatId1, adminId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            Thread t2 = new Thread(() -> {
                try {
                    chatAssignmentManager.assignChat(userChatId2, adminId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            t1.start();
            t2.start();

            //  uso .join() per fare in modo che il test attenda che i thread terminino
            t1.join();
            t2.join();

            assertEquals(1, successCount.get());
            assertEquals(1, failCount.get());
            verify(chatSessionRepository, times(1)).save(any());
        }


    }
}