package com.jh.movieticket.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 채팅방 조회 시 dto
public class ChatRoomVerifyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotBlank(message = "조회할 채팅방의 회원 아이디를 입력해주세요.")
        private String chatRoomMemberId; // 조회할 채팅방의 회원 아이디

        @NotBlank(message = "조회하는 회원의 아이디를 입력해주세요.")
        private String verifyMemberId; // 조회하는 회원의 아이디
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id; // pk
        private String adminId; // 관리자 아이디
        private String memberId; // 회원 아이디
        private long notReadMessage; // 안 읽은 메시지 갯수
    }
}
