package com.jh.movieticket.chat.controller;

import com.jh.movieticket.chat.dto.ChatRoomCreateDto;
import com.jh.movieticket.chat.dto.ChatRoomJoinDto;
import com.jh.movieticket.chat.dto.ChatRoomOutDto;
import com.jh.movieticket.chat.dto.ChatRoomServiceDto;
import com.jh.movieticket.chat.dto.ChatRoomVerifyDto;
import com.jh.movieticket.chat.dto.ChatRoomVerifyDto.Response;
import com.jh.movieticket.chat.service.ChatRoomService;
import com.jh.movieticket.config.GlobalApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
@Validated
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 생성 컨트롤러
     *
     * @param createRequest 채팅방 생성 정보 dto
     * @return 성공 시 201 코드와 생성된 채팅방 dto, 실패 시 에러코드와 에러메시지
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/chatroom")
    public ResponseEntity<GlobalApiResponse<ChatRoomCreateDto.Response>> chatRoomCreateController(
        @Valid @RequestBody ChatRoomCreateDto.Request createRequest) {

        ChatRoomServiceDto chatRoom = chatRoomService.createChatRoom(createRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            GlobalApiResponse.toGlobalResponse(HttpStatus.CREATED, chatRoom.toCreateResponse()));
    }

    /**
     * 채팅방 삭제 컨트롤러
     *
     * @param id 삭제할 채팅방 pk
     * @return 성공 시 200 코드, 실패 시 에러코드와 에러메시지
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/chatroom/{id}")
    public ResponseEntity<GlobalApiResponse<?>> chatRoomDeleteController(
        @Positive(message = "pk값은 0 또는 음수일 수 없습니다.") @PathVariable Long id) {

        chatRoomService.deleteChatRoom(id);

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(HttpStatus.OK, null));
    }

    /**
     * 채팅방 입장 컨트롤러
     *
     * @param joinRequest 입장 정보 dto
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/chatroom/join")
    public void joinChatRoom(@Valid @RequestBody ChatRoomJoinDto.Request joinRequest) {

        chatRoomService.enterChatRoom(joinRequest);
    }

    /**
     * 채팅방 퇴장 컨트롤러
     *
     * @param outRequest 퇴장 정보 dto
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/chatroom/out/{chatRoomId}")
    public void outChatRoom(@Valid @RequestBody ChatRoomOutDto.Request outRequest) {

        chatRoomService.outChatRoom(outRequest);
    }

    /**
     * 채팅방 조회 컨트롤러
     *
     * @param verifyRequest 채팅방 조회 정보 dto
     * @return 성공시 200 코드와 조회된 채팅방 dto, 실패시 에러코드와 에러메시지
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/chatroom")
    public ResponseEntity<GlobalApiResponse<ChatRoomVerifyDto.Response>> chatRoomVerifyController(
        @Valid @RequestBody ChatRoomVerifyDto.Request verifyRequest) {

        ChatRoomServiceDto chatRoomServiceDto = chatRoomService.verifyChatRoom(verifyRequest);

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(HttpStatus.OK,
            chatRoomServiceDto.toVerifyResponse()));
    }

    /**
     * 페이징 처리된 전체 채팅방 리스트 조회 컨트롤러
     *
     * @param verifyMemberId 조회하는 회원의 아이디
     * @param pageable       페이징 정보
     * @return 페이징 처리된 전체 채팅방 dto 리스트
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{verifyMemberId}")
    public ResponseEntity<GlobalApiResponse<Page<ChatRoomVerifyDto.Response>>> chatRoomVerifyAllController(
        @NotBlank(message = "조회하는 회원 아이디를 입력해주세요.") @PathVariable String verifyMemberId,
        @PageableDefault(sort = "id", direction = Direction.ASC)
        Pageable pageable) {

        Page<ChatRoomServiceDto> chatRoomServiceDtoList = chatRoomService.verifyAllChatRoom(
            verifyMemberId,
            pageable);
        List<Response> responseList = chatRoomServiceDtoList.getContent().stream()
            .map(ChatRoomServiceDto::toVerifyResponse)
            .toList();
        Page<Response> responsePage = new PageImpl<>(responseList, pageable, responseList.size());

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(HttpStatus.OK, responsePage));
    }
}
