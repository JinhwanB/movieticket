package com.jh.movieticket.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.movieticket.chat.domain.ChatMessage;
import com.jh.movieticket.chat.domain.ChatRoom;
import com.jh.movieticket.config.JpaAuditingConfig;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.domain.Role;
import com.jh.movieticket.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class ChatMessageRepositoryTest {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @BeforeEach
    void before() {

        Member member = Member.builder()
            .userId("test")
            .userPW("1234")
            .email("test@naver.com")
            .role(Role.ROLE_USER)
            .build();
        Member savedMember = memberRepository.save(member);

        Member admin = Member.builder()
            .role(Role.ROLE_ADMIN)
            .email("admin@naver.com")
            .userId("admin")
            .userPW("1234")
            .build();
        Member savedAdmin = memberRepository.save(admin);

        ChatRoom chatRoom = ChatRoom.builder()
            .admin(savedAdmin)
            .member(savedMember)
            .chatMemberCount(0)
            .notReadMessage(0)
            .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        ChatMessage chatMessage = ChatMessage.builder()
            .message("hihi")
            .chatRoom(savedChatRoom)
            .sender(savedMember)
            .notReadCount(1)
            .build();
        chatMessageRepository.save(chatMessage);

        ChatMessage otherChatMessage = ChatMessage.builder()
            .message("hello")
            .chatRoom(savedChatRoom)
            .sender(savedMember)
            .notReadCount(1)
            .build();
        chatMessageRepository.save(otherChatMessage);
    }

    @Test
    @DisplayName("회원 소프트딜리트로 인한 채팅 메시지 삭제")
    void deleteByMemberSoftDelete() {

        chatMessageRepository.deleteChatMessageByMember(1L);
        List<ChatMessage> chatMessageList = chatMessageRepository.findAll();

        assertThat(chatMessageList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("채팅방을 통해 채팅 메시지 리스트 조회")
    void selectAllChatMessageByChatRoom() {

        ChatRoom chatRoom = chatRoomRepository.findById(1L)
            .orElse(null);

        if (chatRoom != null) {
            List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);

            assertThat(chatMessageList.size()).isEqualTo(2);
        }
    }
}