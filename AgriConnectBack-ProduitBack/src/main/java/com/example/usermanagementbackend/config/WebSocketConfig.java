package com.example.usermanagementbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Broker pour les messages sortants (ex: /topic/notifications)
        config.setApplicationDestinationPrefixes("/app"); // Préfixe pour les messages entrants
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // Endpoint WebSocket
                .setAllowedOrigins("http://localhost:4200") // Autoriser uniquement le frontend Angular
                .withSockJS(); // Support SockJS pour les clients sans WebSocket
    }
}