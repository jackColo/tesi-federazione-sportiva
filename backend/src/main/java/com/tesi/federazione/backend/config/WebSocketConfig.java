package com.tesi.federazione.backend.config;

import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.security.JwtUtils;
import com.tesi.federazione.backend.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Classe per la configurazione dei webSocket che definisce:
 * - endpoint di connessione (STOMP)
 * - regole d'instradamento (Broker)
 * - sicurezza
 */

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Metodo per la configurazione delle regole d'instradamento:
     * - iscrizione del client ai percorsi /topic per la ricezione dei messaggi
     * - ricezione dal client dei messaggi attraverso i percorsi /app
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Metodo per la definizione degli endpoint HTTP a cui il client
     * deve collegarsi per stabilire la connessione iniziale al WebSocket.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins("http://localhost:4200", "http://localhost:8080")
                .withSockJS();
    }

    /**
     * Metodo per la configurazione del canale in entrata (client -> server) con l'inserimento di un interceptor per la
     * gestione dell'autenticazione JWT prima dell'invio al controller.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                // Trasformazione del messaggio generico in un messaggio STOMP
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // Se la connessione è attiva, il token viene estratto e poi validato attraverso le utility JwtUtils (già
                // definite e utilizzate per la connessione HTTP) per poi iniettarlo nella sessione WebSocket.
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        String jwt = authorizationHeader.substring(7);

                        if (jwtUtils.validateJwtToken(jwt)) {
                            String username = jwtUtils.extractUsername(jwt);

                            User userDetails = userDetailsService.loadUserByUsername(username);

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                            // Iniezione del token nella sessione websocket
                            accessor.setUser(authentication);
                            log.info("Connessione webscoket stabilita per l'utente {}", username);
                        }
                    }
                } else {
                    log.error("FAILED: Impossibile stabilire la connessione websocket: token scaduto o invalido");
                }
                return message;
            }
        });
    }

}
