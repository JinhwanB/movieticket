package com.jh.movieticket.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomCreateDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "회원 아이디를 입력해주세요.")
        private String userId; // 회원 아이디

        @NotBlank(message = "관리자 아이디를 입력해주세요.")
        private String adminId; // 관리자 아이디
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id; // pk
        private String adminId; // 관리자 아이디
        private String memberId; // 회원 아이디
        private int chatMemberCount; // 채팅방 멤버 수
        private long notReadMessage; // 안읽은 메시지 갯수
    }
}
