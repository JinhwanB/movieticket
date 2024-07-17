package com.jh.movieticket.chat.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatRoomErrorCode {

    NOT_FOUND_CHAT_ROOM(HttpStatus.BAD_REQUEST.value(), "해당 채팅방이 없습니다.");

    private final int status;
    private final String message;
}
