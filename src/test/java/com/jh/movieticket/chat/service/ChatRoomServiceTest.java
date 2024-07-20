package com.jh.movieticket.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jh.movieticket.chat.domain.ChatMessage;
import com.jh.movieticket.chat.domain.ChatRoom;
import com.jh.movieticket.chat.dto.ChatMessageServiceDto;
import com.jh.movieticket.chat.dto.ChatRoomCreateDto;
import com.jh.movieticket.chat.dto.ChatRoomJoinDto;
import com.jh.movieticket.chat.dto.ChatRoomOutDto;
import com.jh.movieticket.chat.dto.ChatRoomServiceDto;
import com.jh.movieticket.chat.dto.ChatRoomVerifyDto;
import com.jh.movieticket.chat.exception.ChatRoomException;
import com.jh.movieticket.chat.repository.ChatMessageRepository;
import com.jh.movieticket.chat.repository.ChatRoomRepository;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.domain.Role;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ChatRoomServiceTest {

    @MockBean
    ChatRoomRepository chatRoomRepository;

    @MockBean
    ChatMessageRepository chatMessageRepository;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    RedisTemplate<String, ChatMessageServiceDto> chatMessageRedisTemplate;

    @MockBean
    ListOperations<String, ChatMessageServiceDto> listOperations;

    ChatRoomService chatRoomService;
    ChatRoomCreateDto.Request createRequest;
    Member member;
    Member admin;
    ChatRoom chatRoom;
    ChatRoomJoinDto.Request joinRequest;
    List<ChatMessage> chatMessageList;
    ChatMessage chatMessage;
    ChatRoomOutDto.Request outRequest;
    ChatRoomVerifyDto.Request verifyRequest;
    Pageable pageable = PageRequest.of(0, 10, Direction.ASC, "id");
    Page<ChatRoom> chatRoomPageList;

    @BeforeEach
    void before() {

        chatRoomService = new ChatRoomService(chatRoomRepository, chatMessageRepository,
            memberRepository, chatMessageRedisTemplate);

        when(chatMessageRedisTemplate.opsForList()).thenReturn(listOperations);

        createRequest = ChatRoomCreateDto.Request.builder()
            .userId("test")
            .adminId("admin")
            .build();

        member = Member.builder()
            .userId("test")
            .email("test@naver.com")
            .role(Role.ROLE_USER)
            .userPW("1234")
            .build();

        admin = Member.builder()
            .userId("admin")
            .email("admin@naver.com")
            .userPW("1234")
            .role(Role.ROLE_ADMIN)
            .build();

        chatRoom = ChatRoom.builder()
            .chatMemberCount(0)
            .notReadMessage(0)
            .admin(admin)
            .member(member)
            .build();

        joinRequest = ChatRoomJoinDto.Request.builder()
            .chatRoomId(1L)
            .adminId("admin")
            .memberId("test")
            .enterMemberId("test")
            .build();

        chatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .message("hello")
            .notReadCount(0)
            .sender(member)
            .build();

        ChatMessage secondChatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .notReadCount(0)
            .sender(admin)
            .message("hihi")
            .build();

        chatMessageList = List.of(chatMessage, secondChatMessage);

        outRequest = ChatRoomOutDto.Request.builder()
            .chatRoomId(1L)
            .outMemberId("test")
            .build();

        verifyRequest = ChatRoomVerifyDto.Request.builder()
            .chatRoomMemberId("test")
            .verifyMemberId("test")
            .build();

        List<ChatRoom> chatRoomList = List.of(chatRoom);
        chatRoomPageList = new PageImpl<>(chatRoomList, pageable, chatRoomList.size());
    }

    @Test
    @DisplayName("채팅방 생성 서비스")
    void chatRoomCreateService() {

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));
        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(admin));
        when(chatRoomRepository.existsByMember(any())).thenReturn(false);
        when(chatRoomRepository.save(any())).thenReturn(chatRoom);

        ChatRoomServiceDto chatRoomServiceDto = chatRoomService.createChatRoom(createRequest);

        assertThat(chatRoomServiceDto.getMemberId()).isEqualTo("test");
    }

    @Test
    @DisplayName("채팅방 생성 서비스 실패 - 없는 회원")
    void chatRoomCreateServiceFail1() {

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.empty());

        assertThatThrownBy(() -> chatRoomService.createChatRoom(createRequest)).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("채팅방 생성 서비스 실패 - 이미 존재하는 채팅방")
    void chatRoomCreateServiceFail2() {

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));
        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(admin));
        when(chatRoomRepository.existsByMember(any())).thenReturn(true);

        assertThatThrownBy(() -> chatRoomService.createChatRoom(createRequest)).isInstanceOf(
            ChatRoomException.class);
    }

    @Test
    @DisplayName("채팅방 입장 서비스")
    void chatRoomEnterService() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findAllByChatRoom(any())).thenReturn(chatMessageList);

        chatRoomService.enterChatRoom(joinRequest);

        verify(chatRoomRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("채팅방 입장 서비스 실패 - 없는 채팅방")
    void chatRoomEnterServiceFail1() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatRoomService.enterChatRoom(joinRequest)).isInstanceOf(
            ChatRoomException.class);
    }

    @Test
    @DisplayName("채팅방 퇴장 서비스")
    void chatRoomOutService() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.of(chatRoom));

        chatRoomService.outChatRoom(outRequest);

        verify(chatRoomRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("채팅방 퇴장 서비스 실패 - 없는 채팅방")
    void chatRoomOutServiceFail1() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatRoomService.outChatRoom(outRequest)).isInstanceOf(
            ChatRoomException.class);
    }

    @Test
    @DisplayName("채팅방 삭제 서비스")
    void chatRoomDeleteService() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findAllByChatRoom(any())).thenReturn(chatMessageList);
        when(chatMessageRedisTemplate.hasKey(any())).thenReturn(true);

        chatRoomService.deleteChatRoom(1L);

        verify(chatRoomRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("채팅방 삭제 서비스 실패 - 없는 채팅방")
    void chatRoomDeleteServiceFail1() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatRoomService.deleteChatRoom(1L)).isInstanceOf(
            ChatRoomException.class);
    }

    @Test
    @DisplayName("채팅방 조회 서비스")
    void chatRoomVerifyService() {

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));
        when(chatRoomRepository.findByMember(any())).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findAllByChatRoom(any())).thenReturn(chatMessageList);
        when(chatRoomRepository.save(any())).thenReturn(chatRoom);

        ChatRoomServiceDto chatRoomServiceDto = chatRoomService.verifyChatRoom(verifyRequest);

        assertThat(chatRoomServiceDto.getMemberId()).isEqualTo("test");
    }

    @Test
    @DisplayName("채팅방 조회 서비스 실패 - 없는 회원")
    void chatRoomVerifyServiceFail1() {

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.empty());

        assertThatThrownBy(() -> chatRoomService.verifyChatRoom(verifyRequest)).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("채팅방 조회 서비스 실패 - 없는 채팅방")
    void chatRoomVerifyServiceFail2() {

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));
        when(chatRoomRepository.findByMember(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatRoomService.verifyChatRoom(verifyRequest)).isInstanceOf(
            ChatRoomException.class);
    }

    @Test
    @DisplayName("채팅방 전체 리스트 페이징하여 조회 서비스")
    void ChatRoomVerifyAllService() {

        when(chatRoomRepository.findAll(pageable)).thenReturn(chatRoomPageList);
        when(chatMessageRepository.findAllByChatRoom(any())).thenReturn(chatMessageList);
        when(chatRoomRepository.save(any())).thenReturn(chatRoom);

        Page<ChatRoomServiceDto> allChatRoom = chatRoomService.verifyAllChatRoom("test",
            pageable);

        assertThat(allChatRoom.getTotalElements()).isEqualTo(1);
    }
}