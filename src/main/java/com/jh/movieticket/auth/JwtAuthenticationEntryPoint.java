package com.jh.movieticket.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.movieticket.config.GlobalApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

// 401 에러 핸들러
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String NOT_LOGIN = "로그인이 필요합니다.";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException {
        setResponse(response);
    }

    private void setResponse(HttpServletResponse response) throws IOException {

        log.error("로그인 하지 않고 권한이 필요한 요청 보냄");

        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        ObjectMapper objectMapper = new ObjectMapper();
        GlobalApiResponse<Object> result = GlobalApiResponse.toGlobalResponseFail(HttpStatus.UNAUTHORIZED, NOT_LOGIN);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}