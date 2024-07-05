package com.jh.movieticket.theater.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SeatErrorCode {

    NOT_FOUND_SEAT(HttpStatus.BAD_REQUEST.value(), "등록되지 않은 좌석입니다.");

    private final int status;
    private final String message;
}
