package com.jh.movieticket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ChatConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws/chat") // stomp 접속 주소 = ws://localhost:8080/ws/chat
            .setAllowedOrigins("*"); // 모든 origin 허용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/sub"); // 메시지를 구독(수신)하는 요청 엔트포인트
        registry.setApplicationDestinationPrefixes("/pub"); // 메시지를 발송하는 엔드포인트
    }
}
