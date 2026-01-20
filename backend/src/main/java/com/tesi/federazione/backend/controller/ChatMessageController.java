package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.dto.chat.ChatMessageOutputDTO;
import com.tesi.federazione.backend.dto.chat.ChatSummaryDTO;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.security.SecurityUtils;
import com.tesi.federazione.backend.service.chat.ChatMediator;
import com.tesi.federazione.backend.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

/**
 * Controller per l'accesso del client ai metodi che gestiscono la chat tra amministratori e club manager.
 * Gestisce sia il canale WebSocket, sia le API REST per gestire storico e stato delle chat
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final ChatMediator chatMediator;
    private final ChatMessageService chatMessageService;
    private final SecurityUtils securityUtils;

    // SEZIONE WEBSOCKET

    /**
     * Metodo per inviare un messaggio nel canale WebSocket
     *
     * @param message   Messaggio con formato ChatMessageInputDTO
     * @param principal Utente iniettato java.security.Principal per la verifica dei permessi presenti nel token
     */
    @MessageMapping("/chat.send")
    public void SendMessage(@Payload ChatMessageInputDTO message, Principal principal) {
        User currentUser = (User) ((Authentication) principal).getPrincipal();
        chatMediator.routeMessage(message, currentUser);
    }

    // SEZIONE API REST

    /**
     * Metodo per recuperare i messaggi della chat tra amministratori e club manager con ID 'chatUserId'
     *
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @return List<ChatMessageOutputDTO> Lista ordinata dello storico dei messaggi appartenenti alla chat
     */
    @GetMapping("/history/{chatUserId}")
    public ResponseEntity<List<ChatMessageOutputDTO>> getAllMessages(@PathVariable String chatUserId) {
        List<ChatMessageOutputDTO> chatMessages = chatMessageService.getAllChatMessages(chatUserId);
        return new ResponseEntity<>(chatMessages, HttpStatus.OK);
    }

    /**
     * Metodo accessibile agli amministratori di club per prendere in carico la chat di un Club Manager.
     * @param clubManagerId Id del club manager che ha richiesto assistenza.
     */
    @PostMapping("/assign/{clubManagerId}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<?> takeCharge(@PathVariable String clubManagerId) {
        String adminId = securityUtils.getCurrentUserId();
        chatMediator.takeCharge(clubManagerId, adminId);
        return ResponseEntity.ok("Chat presa in carico con successo.");

    }

    /**
     * Metodo accessibile agli amministratori di club per chiudere una sessione attivata per la chat di un Club Manager.
     * @param clubManagerId Id del club manager a cui appartiene la chat.
     */
    @PostMapping("/release/{clubManagerId}")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<?> releaseChat(@PathVariable String clubManagerId) {
        String adminId = securityUtils.getCurrentUserId();
        chatMediator.releaseChat(clubManagerId, adminId);
        return ResponseEntity.ok("Chat rilasciata con successo.");
    }

    /**
     * Metodo per recuperare l'elenco di tutte le chat tra amministratori e club manager
     * @return List<ChatSummaryDTO> Elenco delle chat tra amministratori e club manager
     */
    @GetMapping("/summaries")
    @PreAuthorize("hasAuthority('FEDERATION_MANAGER')")
    public ResponseEntity<List<ChatSummaryDTO>> getChatSummaries() {
        return ResponseEntity.ok(chatMessageService.getChatSummaries());
    }
}