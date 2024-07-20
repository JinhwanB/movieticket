package com.jh.movieticket.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomOutDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request {

        @NotNull(message = "pk값은 null일 수 없습니다.")
        @Positive(message = "pk값은 0 또는 음수일 수 없습니다.")
        private Long chatRoomId; // 채팅방 pk

        @NotBlank(message = "입장하는 회원의 아이디를 입력해주세요.")
        private String outMemberId; // 입장하는 회원 아이디
    }
}
