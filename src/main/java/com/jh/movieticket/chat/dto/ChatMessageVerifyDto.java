package com.jh.movieticket.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 채팅 메시지 조회시 dto
public class ChatMessageVerifyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id; // pk
        private String senderId; // 메시지 보낸 회원 아이디
        private String message; // 보낸 메시지
    }
}
