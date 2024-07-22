package com.jh.movieticket.chat.dto;

import java.io.Serializable;
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
    private int chatMemberCount; // 채팅방 멤버 수
    private long notReadMessage; // 안읽은 메시지 갯수

    /**
     * ServiceDto -> CreateDto
     *
     * @return CreateDto
     */
    public ChatRoomCreateDto.Response toCreateResponse() {

        return ChatRoomCreateDto.Response.builder()
            .id(id)
            .adminId(adminId)
            .memberId(memberId)
            .chatMemberCount(chatMemberCount)
            .notReadMessage(notReadMessage)
            .build();
    }

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
            .notReadMessage(notReadMessage)
            .build();
    }
}
