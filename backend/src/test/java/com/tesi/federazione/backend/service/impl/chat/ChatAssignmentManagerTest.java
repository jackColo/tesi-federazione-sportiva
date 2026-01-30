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
}