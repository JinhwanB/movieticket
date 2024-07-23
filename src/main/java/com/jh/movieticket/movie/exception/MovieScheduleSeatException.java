package com.jh.movieticket.movie.exception;

import lombok.Getter;

@Getter
public class MovieScheduleSeatException extends RuntimeException {

    private final MovieScheduleSeatErrorCode movieScheduleSeatErrorCode;

    public MovieScheduleSeatException(MovieScheduleSeatErrorCode movieScheduleSeatErrorCode) {

        super(movieScheduleSeatErrorCode.getMessage());
        this.movieScheduleSeatErrorCode = movieScheduleSeatErrorCode;
    }
}
