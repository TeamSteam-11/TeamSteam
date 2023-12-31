package com.ll.TeamSteam.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 메시지브로커를 등록하는 코드
        config.setApplicationDestinationPrefixes("/app"); // 도착 경로에 대한 prefix를 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")   //SockJS 연결 주소
                .setAllowedOriginPatterns("*")
                .setAllowedOrigins("http://localhost:8080")
                .setAllowedOrigins("https://www.teamsteam.site")// spring stomp CORS 설정하기
                .withSockJS() // 주소 : ws://localhost:8080/ws
                .setInterceptors(new HttpSessionHandshakeInterceptor());
    }
}
