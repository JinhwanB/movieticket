package com.jh.movieticket.theater.exception;

import lombok.Getter;

@Getter
public class TheaterException extends RuntimeException{

    private final TheaterErrorCode theaterErrorCode;

    public TheaterException(TheaterErrorCode theaterErrorCode){

        super(theaterErrorCode.getMessage());
        this.theaterErrorCode = theaterErrorCode;
    }
}
