package com.jh.movieticket.reservation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReservationErrorCode {

    NOT_FOUND_RESERVATION(HttpStatus.BAD_REQUEST.value(), "해당하는 예약이 존재하지 않습니다.");

    private final int status;
    private final String message;
}
