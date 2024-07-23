package com.jh.movieticket.movie.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MovieScheduleSeatErrorCode {

    NOT_FOUND_MOVIE_SCHEDULE_SEAT(HttpStatus.BAD_REQUEST.value(), "상영관에 존재하지 않는 좌석입니다.");

    private final int status;
    private final String message;
}
