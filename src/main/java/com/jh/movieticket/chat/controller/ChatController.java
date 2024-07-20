package com.jh.movieticket.chat.controller;

import com.jh.movieticket.chat.dto.ChatMessageSendDto;
import com.jh.movieticket.chat.service.ChatMessageService;
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
    private final ChatMessageService chatMessageService;

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
