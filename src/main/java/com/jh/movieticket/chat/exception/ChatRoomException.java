package com.jh.movieticket.chat.exception;

import lombok.Getter;

@Getter
public class ChatRoomException extends RuntimeException {

    private final ChatRoomErrorCode chatRoomErrorCode;

    public ChatRoomException(ChatRoomErrorCode chatRoomErrorCode) {
        super(chatRoomErrorCode.getMessage());
        this.chatRoomErrorCode = chatRoomErrorCode;
    }
}
