package com.jh.movieticket.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {
    EXPIRED_TOKEN(400, "요청한 토큰은 만료된 토큰입니다. 재발급이 필요합니다.");

    private final int status;
    private final String message;
}
