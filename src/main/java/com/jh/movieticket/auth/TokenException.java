package com.jh.movieticket.auth;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException{

    private final TokenErrorCode tokenErrorCode;

    public TokenException(TokenErrorCode tokenErrorCode){
        super(tokenErrorCode.getMessage());
        this.tokenErrorCode = tokenErrorCode;
    }
}
