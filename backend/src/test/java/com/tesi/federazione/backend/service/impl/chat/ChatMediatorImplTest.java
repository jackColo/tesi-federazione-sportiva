package com.tesi.federazione.backend.service.impl.chat;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.ChatMessage;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.service.chat.ChatMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMediatorImplTest {

    @Mock
    private ChatAssignmentManager assignmentManager;

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatMediatorImpl chatMediator;

    @Nested
    @DisplayName("Tests per: routeMessage()")
    class RouteMessageTest {

        @Test
        @DisplayName("SUCCESSO: Club Manager invia messaggio nella propria chat")
        void success_ClubManager() {

            String userId = "userId";
            User user = new User();
            user.setId(userId);
            user.setRole(Role.CLUB_MANAGER);

            ChatMessageInputDTO dto = new ChatMessageInputDTO();
            dto.setChatUserId(userId);
            dto.setMessage("Ciao");

            ChatMessage savedMsg = new ChatMessage();
            savedMsg.setContent("Ciao");

            when(chatMessageService.saveChatMessage(dto, userId, Role.CLUB_MANAGER))
                    .thenReturn(savedMsg);

            chatMediator.routeMessage(dto, user);

            verify(chatMessageService).saveChatMessage(dto, userId, Role.CLUB_MANAGER);
            verify(messagingTemplate).convertAndSend(eq("/topic/user/" + userId), eq(savedMsg));
        }

        @Test
        @DisplayName("SUCCESSO: Federation Manager invia messaggio in chat presa in carico")
        void success_FederationManager() {
            String adminId = "adminId";
            String clubManagerId = "clubId";

            User admin = new User();
            admin.setId(adminId);
            admin.setRole(Role.FEDERATION_MANAGER);

            ChatMessageInputDTO dto = new ChatMessageInputDTO();
            dto.setChatUserId(clubManagerId);
            dto.setMessage("Ciao");

            ChatMessage savedMsg = new ChatMessage();

            when(assignmentManager.getCurrentAdminForClubManager(clubManagerId)).thenReturn(adminId);

            when(chatMessageService.saveChatMessage(dto, adminId, Role.FEDERATION_MANAGER))
                    .thenReturn(savedMsg);

            chatMediator.routeMessage(dto, admin);

            verify(messagingTemplate).convertAndSend(eq("/topic/user/" + clubManagerId), eq(savedMsg));
        }

        @Test
        @DisplayName("FALLIMENTO: Club Manager tenta di scrivere nella chat di un altro club manager")
        void fail_clubManagerUnauthorized() {
            User user = new User();
            user.setId("userId");
            user.setRole(Role.CLUB_MANAGER);

            ChatMessageInputDTO dto = new ChatMessageInputDTO();
            dto.setChatUserId("other-userId");

            assertThrows(ActionNotAllowedException.class, () ->
                    chatMediator.routeMessage(dto, user)
            );

            verify(messagingTemplate, never()).convertAndSend(anyString(), any(ChatMessage.class));
            verify(chatMessageService, never()).saveChatMessage(any(), any(), any());
        }

        @Test
        @DisplayName("FALLIMENTO: Atleta tenta di inviare messaggio")
        void fail_athleteUnauthorized() {
            User user = new User();
            user.setId("userId");
            user.setRole(Role.ATHLETE);

            ChatMessageInputDTO dto = new ChatMessageInputDTO();
            dto.setChatUserId("userId");

            assertThrows(ActionNotAllowedException.class, () ->
                    chatMediator.routeMessage(dto, user)
            );

            verify(messagingTemplate, never()).convertAndSend(anyString(), any(ChatMessage.class));
            verify(chatMessageService, never()).saveChatMessage(any(), any(), any());
        }

        @Test
        @DisplayName("FALLIMENTO: Admin tenta di scrivere senza aver preso in carico la chat")
        void fail_adminNotAssigned() {
            String adminId = "adminId";
            String clubManagerId = "clubId";

            User admin = new User();
            admin.setId(adminId);
            admin.setRole(Role.FEDERATION_MANAGER);

            ChatMessageInputDTO dto = new ChatMessageInputDTO();
            dto.setChatUserId(clubManagerId);

            when(assignmentManager.getCurrentAdminForClubManager(clubManagerId)).thenReturn(null);

            assertThrows(ActionNotAllowedException.class, () ->
                    chatMediator.routeMessage(dto, admin)
            );

            verify(chatMessageService, never()).saveChatMessage(any(), any(), any());
            verify(messagingTemplate, never()).convertAndSend(anyString(), any(ChatMessage.class));
        }
    }

    @Nested
    @DisplayName("Tests per: takeCharge()")
    class TakeChargeTest {

        @Test
        @DisplayName("SUCCESSO: viene chiamato l'assignmentManager")
        void testTakeCharge() {
            String adminId = "adminId";
            String clubManagerId = "clubId";

            chatMediator.takeCharge(clubManagerId, adminId);

            verify(assignmentManager, times(1)).assignChat(clubManagerId, adminId);
        }
    }

    @Nested
    @DisplayName("Tests per: releaseChat()")
    class ReleaseChatTests {

        @Test
        @DisplayName("SUCCESSO: viene chiamato l'assignmentManager")
        void testReleaseChat() {
            String clubManagerId = "clubId";

            chatMediator.releaseChat(clubManagerId);

            verify(assignmentManager, times(1)).releaseChat(clubManagerId);
        }
    }
}