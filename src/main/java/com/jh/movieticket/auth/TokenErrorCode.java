package com.jh.movieticket.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.BAD_REQUEST.value(), "refreshToken이 없습니다. 재로그인이 필요합니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST.value(), "만료된 refreshToken입니다. 재로그인이 필요합니다."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST.value(), "요청한 토큰은 만료된 토큰입니다. 재발급이 필요합니다.");

    private final int status;
    private final String message;
}
