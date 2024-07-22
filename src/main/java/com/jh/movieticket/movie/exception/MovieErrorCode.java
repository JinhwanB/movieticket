package com.jh.movieticket.movie.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MovieErrorCode {

    EXIST_MOVIE_TITLE(HttpStatus.BAD_REQUEST.value(), "이미 등록되어있는 영화입니다."),
    NOT_FOUND_MOVIE(HttpStatus.BAD_REQUEST.value(), "등록되지 않은 영화입니다.");

    private final int status;
    private final String message;
}
