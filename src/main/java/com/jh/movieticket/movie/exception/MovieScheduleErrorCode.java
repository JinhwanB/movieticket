package com.jh.movieticket.movie.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MovieScheduleErrorCode {

    NOT_FOUND_MOVIE_SCHEDULE(HttpStatus.BAD_REQUEST.value(), "해당하는 영화스케줄이 존재하지 않습니다.");

    private final int status;
    private final String message;
}
