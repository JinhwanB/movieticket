package com.jh.movieticket.chat.controller;

import com.jh.movieticket.chat.dto.ChatMessageServiceDto;
import com.jh.movieticket.chat.dto.ChatMessageVerifyDto;
import com.jh.movieticket.chat.dto.ChatMessageVerifyDto.Response;
import com.jh.movieticket.chat.service.ChatMessageService;
import com.jh.movieticket.config.GlobalApiResponse;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatmessages")
@RequiredArgsConstructor
@Validated
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * 채팅방의 채팅메시지 리스트 조회 컨트롤러
     *
     * @param chatRoomId 채팅방 pk
     * @return 성공 시 200 코드와 채팅 메시지 dto 리스트, 실패 시 에러코드와 에러메시지
     */
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<GlobalApiResponse<List<ChatMessageVerifyDto.Response>>> chatMessageVerifyAllController(
        @Positive(message = "pk값은 0 또는 음수일 수 없습니다.") @PathVariable Long chatRoomId) {

        List<ChatMessageServiceDto> chatMessageServiceDtos = chatMessageService.chatMessageVerifyAll(
            chatRoomId);
        List<Response> responseList = chatMessageServiceDtos.stream()
            .map(ChatMessageServiceDto::toVerifyResponse)
            .toList();

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(HttpStatus.OK, responseList));
    }
}
