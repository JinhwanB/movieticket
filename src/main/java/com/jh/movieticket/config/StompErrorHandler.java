package com.jh.movieticket.config;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private static final String NOT_AUTH_MESSAGE = "채팅은 로그인이 필요한 서비스 입니다.";
    private static final String UNAVAILABLE_AUTH_MESSAGE = "유효하지 않은 토큰입니다.";

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage,
        Throwable ex) {

        log.error(ex.getCause().getMessage(), ex);

        if (ex.getCause().getMessage().equals(NOT_AUTH_MESSAGE) || ex.getCause().getMessage()
            .equals(UNAVAILABLE_AUTH_MESSAGE)) {
            return errorMessage(ex.getCause().getMessage());
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    // 오류 메시지를 포함한 Message 객체 생성
    private Message<byte[]> errorMessage(String message) {

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message.getBytes(StandardCharsets.UTF_8),
            accessor.getMessageHeaders());
    }
}
