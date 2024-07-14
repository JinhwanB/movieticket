package com.jh.movieticket.movie.exception;

import lombok.Getter;

@Getter
public class MovieException extends RuntimeException{

    private final MovieErrorCode movieErrorCode;

    public MovieException(MovieErrorCode movieErrorCode){

        super(movieErrorCode.getMessage());
        this.movieErrorCode = movieErrorCode;
    }
}
