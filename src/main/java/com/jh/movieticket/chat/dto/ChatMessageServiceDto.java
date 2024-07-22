package com.jh.movieticket.chat.dto;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 채팅메시지 서비스 레이어 dto
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class ChatMessageServiceDto implements Serializable {

    private Long id; // pk
    private String senderId; // 메시지 보낸 회원 아이디
    private String message; // 보낸 메시지
    private int notReadCount; // 안읽은 사람 수

    /**
     * ServiceDto -> VerifyResponse
     *
     * @return VerifyResponse
     */
    public ChatMessageVerifyDto.Response toVerifyResponse() {

        return ChatMessageVerifyDto.Response.builder()
            .id(id)
            .senderId(senderId)
            .message(message)
            .build();
    }
}
