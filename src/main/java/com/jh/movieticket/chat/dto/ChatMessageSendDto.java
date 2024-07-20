package com.jh.movieticket.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class ChatMessageSendDto {

    @NotNull(message = "pk값은 null일 수 없습니다.")
    @Positive(message = "pk값은 0 또는 음수일 수 없습니다.")
    private Long roomId; // 채팅방

    @NotBlank(message = "메시지 작성자 아이디를 입력해주세요.")
    private String sender; // 작성자

    @NotBlank(message = "메시지 내용을 입력해주세요.")
    private String message; // 메시지
}
