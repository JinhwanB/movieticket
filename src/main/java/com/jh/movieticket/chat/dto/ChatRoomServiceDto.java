package com.jh.movieticket.chat.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 치팅방 서비스 레이어 dto
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class ChatRoomServiceDto implements Serializable {

    private Long id; // pk
    private String adminId; // 관리자 아이디
    private String memberId; // 회원 아이디
    private List<ChatMessageServiceDto> chatMessageList; // 채팅메시지

    /**
     * ServiceDto -> VerifyResponse
     *
     * @return VerifyResponse
     */
    public ChatRoomVerifyDto.Response toVerifyResponse() {

        return ChatRoomVerifyDto.Response.builder()
            .id(id)
            .adminId(adminId)
            .memberId(memberId)
            .chatMessageList(chatMessageList)
            .build();
    }
}
