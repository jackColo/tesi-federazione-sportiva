package com.tesi.federazione.backend.service.impl.chat;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.dto.chat.ChatMessageOutputDTO;
import com.tesi.federazione.backend.dto.chat.ChatSummaryDTO;
import com.tesi.federazione.backend.exception.UnauthorizedException;
import com.tesi.federazione.backend.mapper.ChatMessageMapper;
import com.tesi.federazione.backend.model.ChatMessage;
import com.tesi.federazione.backend.model.ChatSession;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.ChatMessageRepository;
import com.tesi.federazione.backend.repository.ChatSessionRepository;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatSessionRepository chatSessionRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ChatMessageMapper chatMessageMapper;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    @Nested
    @DisplayName("Tests per: getAllChatMessages()")
    class GetAllMessagesTests {

        @Test
        @DisplayName("SUCCESSO: Federation Manager visualizza messaggi di ogni chat")
        void success_federationManager() {
            String clubManagerId = "clubId";
            ChatMessage msg = new ChatMessage();

            when(chatMessageRepository.findByChatUserIdOrderByTimestampAsc(clubManagerId))
                    .thenReturn(List.of(msg));

            when(securityUtils.isClubManager()).thenReturn(false);
            when(chatMessageMapper.toDTO(msg)).thenReturn(new ChatMessageOutputDTO());

            List<ChatMessageOutputDTO> result = chatMessageService.getAllChatMessages(clubManagerId);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(chatMessageRepository).findByChatUserIdOrderByTimestampAsc(clubManagerId);
            verify(chatMessageMapper).toDTO(msg);
        }

        @Test
        @DisplayName("SUCCESSO: Club Manager visualizza la propria chat")
        void success_ClubManager() {
            String userId = "userId";
            ChatMessage msg = new ChatMessage();

            when(chatMessageRepository.findByChatUserIdOrderByTimestampAsc(userId))
                    .thenReturn(List.of(msg));

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn(userId);
            when(chatMessageMapper.toDTO(msg)).thenReturn(new ChatMessageOutputDTO());

            List<ChatMessageOutputDTO> result = chatMessageService.getAllChatMessages(userId);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(chatMessageRepository).findByChatUserIdOrderByTimestampAsc(userId);
            verify(chatMessageMapper).toDTO(msg);
        }

        @Test
        @DisplayName("FALLIMENTO: Club Manager tenta di leggere chat altrui")
        void fail_clubManagerUnauthorized() {
            String userId = "userId";
            String clubManagerId = "clubId";

            when(chatMessageRepository.findByChatUserIdOrderByTimestampAsc(clubManagerId))
                    .thenReturn(Collections.emptyList());

            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn(userId);

            assertThrows(UnauthorizedException.class, () ->
                    chatMessageService.getAllChatMessages(clubManagerId)
            );

            verify(chatMessageMapper, never()).toDTO(any());
        }
    }

    @Test
    @DisplayName("SUCCESSO: Test per saveChatMessage")
    void saveMessageTest_success() {
            String senderId = "senderId";
            Role role = Role.CLUB_MANAGER;
            ChatMessageInputDTO input = new ChatMessageInputDTO();
            input.setChatUserId("chatId");
            input.setMessage("Ciao");

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId("id");
            chatMessage.setSenderId(senderId);
            chatMessage.setChatUserId("chatId");
            chatMessage.setContent("Ciao");

            when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

            ChatMessage result = chatMessageService.saveChatMessage(input, senderId, role);

            assertNotNull(result);
            ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
            verify(chatMessageRepository).save(captor.capture());
            ChatMessage captured = captor.getValue();

            assertEquals("chatId", captured.getChatUserId());
            assertEquals("Ciao", captured.getContent());
            assertEquals(senderId, captured.getSenderId());
            assertEquals(role, captured.getSenderRole());
            assertNotNull(captured.getTimestamp());
        }


    @Nested
    @DisplayName("Test per: getChatSummaries()")
    class GetSummariesTest {

        @Test
        @DisplayName("SUCCESSO: Generazione sommario con ordinamento e stati corretti")
        void success_summariesCreated() {
            String userId1 = "userId1";
            User user1 = new User();
            user1.setId(userId1);
            user1.setFirstName("Mario");
            user1.setLastName("Rossi");

            String userId2 = "userId2";
            User user2 = new User();
            user2.setId(userId2);
            user2.setFirstName("Luigi");
            user2.setLastName("Verdi");

            when(userRepository.findByRole(Role.CLUB_MANAGER)).thenReturn(List.of(user1, user2));

            ChatSession session1 = new ChatSession();
            session1.setAdminId("admin");
            when(chatSessionRepository.findByClubManagerIdAndActiveTrue(userId1))
                    .thenReturn(Optional.of(session1));

            ChatMessage msg1 = new ChatMessage();
            msg1.setSenderId("admin");
            msg1.setTimestamp(LocalDateTime.now().minusMinutes(5));
            when(chatMessageRepository.findByChatUserIdOrderByTimestampDesc(eq(userId1), any(Pageable.class)))
                    .thenReturn(List.of(msg1));

            when(chatSessionRepository.findByClubManagerIdAndActiveTrue(userId2))
                    .thenReturn(Optional.empty());

            ChatMessage msg2 = new ChatMessage();
            msg2.setSenderId(userId2);
            msg2.setTimestamp(LocalDateTime.now().minusMinutes(1));
            when(chatMessageRepository.findByChatUserIdOrderByTimestampDesc(eq(userId2), any(Pageable.class)))
                    .thenReturn(List.of(msg2));

            List<ChatSummaryDTO> result = chatMessageService.getChatSummaries();

            assertEquals(2, result.size());

            // Verifica Ordinamento:
            // user2 deve essere primo perché isWaitingForReply=true, anche se il messaggio di user1 è più recente
            ChatSummaryDTO summary1 = result.get(0);
            assertEquals(userId2, summary1.getChatUserId());
            assertEquals("Luigi Verdi", summary1.getClubManagerName());
            assertEquals("FREE", summary1.getStatus());
            assertNull(summary1.getAssignedAdminId());
            assertTrue(summary1.isWaitingForReply());

            ChatSummaryDTO summary2 = result.get(1);
            assertEquals(userId1, summary2.getChatUserId());
            assertEquals("Mario Rossi", summary2.getClubManagerName());
            assertEquals("ASSIGNED", summary2.getStatus());
            assertEquals("admin",summary2.getAssignedAdminId());
            assertFalse(summary2.isWaitingForReply());
        }

        @Test
        @DisplayName("SUCCESSO: nessuna chat presente")
        void success_NoMessages() {
            when(userRepository.findByRole(Role.CLUB_MANAGER)).thenReturn(Collections.emptyList());
            List<ChatSummaryDTO> result = chatMessageService.getChatSummaries();

            assertEquals(0, result.size());
        }
    }
}