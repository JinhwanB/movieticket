package com.jh.movieticket.movie.exception;

import lombok.Getter;

@Getter
public class PosterException extends RuntimeException{

    private final PosterErrorCode posterErrorCode;

    public PosterException(PosterErrorCode posterErrorCode){

        super(posterErrorCode.getMessage());
        this.posterErrorCode = posterErrorCode;
    }
}
