package com.jh.movieticket.theater.exception;

import lombok.Getter;

@Getter
public class SeatException extends RuntimeException{

    private final SeatErrorCode seatErrorCode;

    public SeatException(SeatErrorCode seatErrorCode){

        super(seatErrorCode.getMessage());
        this.seatErrorCode = seatErrorCode;
    }
}
