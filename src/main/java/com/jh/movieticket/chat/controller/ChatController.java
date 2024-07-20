package com.jh.movieticket.chat.controller;

import com.jh.movieticket.chat.dto.ChatMessageSendDto;
import com.jh.movieticket.chat.dto.ChatRoomJoinDto;
import com.jh.movieticket.chat.dto.ChatRoomOutDto;
import com.jh.movieticket.chat.service.ChatMessageService;
import com.jh.movieticket.chat.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
@Validated
public class ChatController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    /**
     * 채팅방 입장 컨트롤러
     *
     * @param joinRequest 입장 정보 dto
     */
    @MessageMapping("/chat/join")
    public void joinChatRoom(@Valid @RequestBody ChatRoomJoinDto.Request joinRequest) {

        chatRoomService.enterChatRoom(joinRequest);
    }

    /**
     * 채팅방 퇴장 컨트롤러
     *
     * @param outRequest 퇴장 정보 dto
     */
    @MessageMapping("/chat/out/{chatRoomId}")
    public void outChatRoom(@Valid @RequestBody ChatRoomOutDto.Request outRequest) {

        chatRoomService.outChatRoom(outRequest);
    }

    /**
     * 메시지 발송하고 db에 저장하는 컨트롤러
     *
     * @param chatMessageSendDto 발송할 메시지 내용 dto
     */
    @MessageMapping("/chat/message")
    public void message(@Valid @RequestBody ChatMessageSendDto chatMessageSendDto) {

        chatMessageService.chatMessageSave(chatMessageSendDto);

        simpMessageSendingOperations.convertAndSend(
            "/sub/chat/room" + chatMessageSendDto.getRoomId(), chatMessageSendDto);
    }
}
