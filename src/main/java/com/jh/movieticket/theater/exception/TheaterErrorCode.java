package com.jh.movieticket.theater.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TheaterErrorCode {

    NOT_FOUND_THEATER(HttpStatus.BAD_REQUEST.value(), "등록되지 않은 상영관입니다.");

    private final int status;
    private final String message;
}
