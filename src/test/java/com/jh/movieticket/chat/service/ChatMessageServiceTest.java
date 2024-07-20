package com.jh.movieticket.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jh.movieticket.chat.domain.ChatMessage;
import com.jh.movieticket.chat.domain.ChatRoom;
import com.jh.movieticket.chat.dto.ChatMessageSendDto;
import com.jh.movieticket.chat.dto.ChatMessageServiceDto;
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
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ChatMessageServiceTest {

    @MockBean
    ChatMessageRepository chatMessageRepository;

    @MockBean
    ChatRoomRepository chatRoomRepository;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    RedisTemplate<String, ChatMessageServiceDto> chatMessageRedisTemplate;

    @MockBean
    ListOperations<String, ChatMessageServiceDto> listOperations;

    ChatMessageService chatMessageService;
    ChatMessageSendDto chatMessageSendDto;
    ChatRoom chatRoom;
    Member member;
    ChatMessage chatMessage;
    List<ChatMessage> chatMessageList;

    @BeforeEach
    void before() {

        chatMessageService = new ChatMessageService(chatMessageRepository, chatRoomRepository,
            memberRepository, chatMessageRedisTemplate);

        when(chatMessageRedisTemplate.opsForList()).thenReturn(listOperations);

        chatMessageSendDto = ChatMessageSendDto.builder()
            .message("hello")
            .sender("test")
            .roomId(1L)
            .build();

        member = Member.builder()
            .email("test@naver.com")
            .role(Role.ROLE_USER)
            .userId("test")
            .userPW("1234")
            .build();

        Member admin = Member.builder()
            .userId("admin")
            .userPW("1234")
            .email("admin@naver.com")
            .role(Role.ROLE_ADMIN)
            .build();

        chatRoom = ChatRoom.builder()
            .chatMemberCount(0)
            .notReadMessage(0)
            .admin(admin)
            .member(member)
            .build();

        chatMessage = ChatMessage.builder()
            .sender(member)
            .message("hello")
            .notReadCount(0)
            .chatRoom(chatRoom)
            .build();

        chatMessageList = List.of(chatMessage);
    }

    @Test
    @DisplayName("채팅 메시지 저장 서비스")
    void chatMessageSaveService() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));
        when(chatMessageRepository.save(any())).thenReturn(chatMessage);

        chatMessageService.chatMessageSave(chatMessageSendDto);

        verify(chatMessageRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("채팅 메시지 저장 서비스 실패 - 없는 채팅방")
    void chatMessageSaveServiceFail1() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(
            () -> chatMessageService.chatMessageSave(chatMessageSendDto)).isInstanceOf(
            ChatRoomException.class);
    }

    @Test
    @DisplayName("채팅 메시지 저장 서비스 실패 - 없는 회원")
    void chatMessageSaveServiceFail2() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(
            () -> chatMessageService.chatMessageSave(chatMessageSendDto)).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("채팅방의 전체 채팅 메시지 리스트 조회 서비스")
    void chatMessageVerifyAllService() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findAllByChatRoom(any())).thenReturn(chatMessageList);

        List<ChatMessageServiceDto> chatMessageServiceDtos = chatMessageService.chatMessageVerifyAll(
            1L);

        assertThat(chatMessageServiceDtos.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("채팅방의 전체 채팅 메시지 리스트 조회 서비스 실패 - 없는 채팅방")
    void chatMessageVerifyAllServiceFail1() {

        when(chatRoomRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(
            () -> chatMessageService.chatMessageSave(chatMessageSendDto)).isInstanceOf(
            ChatRoomException.class);
    }
}