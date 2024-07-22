package com.jh.movieticket.chat.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatRoomErrorCode {

    FAIL_TO_OUT(HttpStatus.BAD_REQUEST.value(), "해당 채팅방에 입장한 회원이 아닙니다."),
    FAIL_TO_ENTER(HttpStatus.BAD_REQUEST.value(), "해당 채팅방에 입장할 수 없는 회원입니다."),
    EXIST_CHAT_ROOM(HttpStatus.BAD_REQUEST.value(), "이미 채팅방이 존재합니다."),
    NOT_FOUND_CHAT_ROOM(HttpStatus.BAD_REQUEST.value(), "해당 채팅방이 없습니다.");

    private final int status;
    private final String message;
}
