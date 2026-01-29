package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.dto.chat.ChatMessageOutputDTO;
import com.tesi.federazione.backend.dto.chat.ChatSummaryDTO;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.security.JwtUtils;
import com.tesi.federazione.backend.security.SecurityUtils;
import com.tesi.federazione.backend.service.chat.ChatMediator;
import com.tesi.federazione.backend.service.chat.ChatMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatMessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatMediator chatMediator;

    @MockitoBean
    private ChatMessageService chatMessageService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private SecurityUtils securityUtils;


    @Test
    @DisplayName("Test per SendMessage()")
    void sendMessageTest() {
        // Questo test non fa mock della chiamata perchè non utilizza HTTP ma Websocket
        // il test ha lo scopo di verificare che il ChatMediator venga correttamente chiamato
        // viene creata un'istanza del controller per poter richiamare la funzione utilizzata
        // dalla connessione webSocket
        ChatMessageInputDTO chatMessageInputDTO = new ChatMessageInputDTO();
        User user = mock(User.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(user);

        ChatMessageController chatMessageController = new ChatMessageController(
                chatMediator,
                chatMessageService,
                securityUtils
        );

        chatMessageController.SendMessage(chatMessageInputDTO, authentication);
        verify(chatMediator).routeMessage(chatMessageInputDTO, user);
    }

    @Test
    @DisplayName("Test per GET /history/{chatUserId}")
    void getAllMessagesTest() throws Exception {
        String chatUserId = "chatId";
        ChatMessageOutputDTO outputDTO = new ChatMessageOutputDTO();
        outputDTO.setContent("Messaggio test");
        
        when(chatMessageService.getAllChatMessages(chatUserId)).thenReturn(List.of(outputDTO));

        mockMvc.perform(get("/api/chat/history/{chatUserId}", chatUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].content").value("Messaggio test"));
    }

    @Test
    @DisplayName("Test per POST /assign/{clubManagerId}")
    void takeChargeTest() throws Exception {
        String clubManagerId = "chatId";
        String adminId = "adminId";

        when(securityUtils.getCurrentUserId()).thenReturn(adminId);

        mockMvc.perform(post("/api/chat/assign/{clubManagerId}", clubManagerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Chat presa in carico con successo."));

        verify(chatMediator).takeCharge(clubManagerId, adminId);
    }

    @Test
    @DisplayName("Test per POST /release/{clubManagerId}")
    void releaseChatTest() throws Exception {
        String clubManagerId = "chatId";

        mockMvc.perform(post("/api/chat/release/{clubManagerId}", clubManagerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Chat rilasciata con successo."));

        verify(chatMediator).releaseChat(eq(clubManagerId));
    }

    @Test
    @DisplayName("Test per GET /summaries")
    void getChatSummariesTest() throws Exception {
        ChatSummaryDTO summary = new ChatSummaryDTO();
        summary.setChatUserId("chatId");
        summary.setStatus("FREE");

        when(chatMessageService.getChatSummaries()).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/chat/summaries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].chatUserId").value("chatId"))
                .andExpect(jsonPath("$[0].status").value("FREE"));
    }
}