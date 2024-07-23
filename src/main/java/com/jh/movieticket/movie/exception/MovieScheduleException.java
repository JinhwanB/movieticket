package com.jh.movieticket.movie.exception;

import lombok.Getter;

@Getter
public class MovieScheduleException extends RuntimeException {

    private final MovieScheduleErrorCode movieScheduleErrorCode;

    public MovieScheduleException(MovieScheduleErrorCode movieScheduleErrorCode) {

        super(movieScheduleErrorCode.getMessage());
        this.movieScheduleErrorCode = movieScheduleErrorCode;
    }
}
