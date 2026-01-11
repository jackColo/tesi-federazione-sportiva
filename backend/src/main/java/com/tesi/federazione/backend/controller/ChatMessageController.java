package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.chat.ChatMessageInputDTO;
import com.tesi.federazione.backend.dto.chat.ChatMessageOutputDTO;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.service.ChatMediator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

/**
 * Controller per l'accesso del client ai metodi che gestiscono la chat tra amministratori e club manager
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final ChatMediator chatMediator;

    /**
     * Metodo per inviare un messaggio nel canale WebSocket
     * @param message Messaggio con formato ChatMessageInputDTO
     * @param principal Utente iniettato java.security.Principal per la verifica dei permessi presenti nel token
     */
    @MessageMapping("/chat.send")
    public void SendMessage(@Payload ChatMessageInputDTO message, Principal principal) {
        User currentUser = (User) ((Authentication) principal).getPrincipal();
        chatMediator.routeMessage(message, currentUser);
    }

    /**
     * Metodo per recuperare i messaggi della chat tra amministratori e club manager con ID 'chatUserId'
     * @param chatUserId Id della chat tra amministratori e clubManager
     * @return List<ChatMessageOutputDTO> Lista ordinata dello storico dei messaggi appartenenti alla chat
     */
    @GetMapping("/history/{chatUserId}")
    public ResponseEntity<List<ChatMessageOutputDTO>> getAllMessages(@PathVariable String chatUserId) {
        List<ChatMessageOutputDTO> chatMessages = chatMediator.getAllChatMessages(chatUserId);
        return new ResponseEntity<>(chatMessages, HttpStatus.OK);
    }
}
