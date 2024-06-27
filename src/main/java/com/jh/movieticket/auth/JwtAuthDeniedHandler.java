package com.jh.movieticket.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.movieticket.config.GlobalApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

// 권한 실패 핸들러
@Slf4j
public class JwtAuthDeniedHandler implements AccessDeniedHandler {

    private static final String NOT_AUTH = "권한이 없습니다.";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {
        setResponse(response);
    }

    private void setResponse(HttpServletResponse response) throws IOException {

        log.error("권한 없음");

        response.setStatus(403);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        ObjectMapper objectMapper = new ObjectMapper();
        GlobalApiResponse<Object> result = GlobalApiResponse.toGlobalResponseFail(403, NOT_AUTH);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}