package com.jh.movieticket.config;

import com.jh.movieticket.auth.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider tokenProvider;

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    // 메시지가 전송되기 전에 가로채서 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT
            == accessor.getCommand()) { // 연결 시도(채팅방 입장 시)시 헤더의 토큰을 통해 회원 권한 인증 진행
            String authorization = accessor.getFirstNativeHeader(TOKEN_HEADER);

            if (!ObjectUtils.isEmpty(authorization) && authorization.startsWith(TOKEN_PREFIX)) {
                String token = authorization.substring(TOKEN_PREFIX.length());

                if (StringUtils.hasText(token) && tokenProvider.validateToken(
                    token)) { // 토큰 검증 후 올바른 토큰일 시
                    Authentication auth = tokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    log.error("채팅 입장 불가 - 토큰 검증 실패");
                    throw new AccessDeniedException("유효하지 않은 토큰입니다.");
                }
            } else {
                log.error("채팅 입장 불가 - 로그인 x");
                throw new AccessDeniedException("채팅은 로그인이 필요한 서비스 입니다.");
            }
        }

        return message;
    }
}
