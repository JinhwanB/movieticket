package com.jh.movieticket.member.service;


import static org.assertj.core.api.Assertions.assertThat;

import com.jh.movieticket.chat.domain.ChatMessage;
import com.jh.movieticket.chat.domain.ChatRoom;
import com.jh.movieticket.chat.repository.ChatMessageRepository;
import com.jh.movieticket.chat.repository.ChatRoomRepository;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.domain.Role;
import com.jh.movieticket.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberDeleteServiceTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    MemberService memberService;

    @BeforeEach
    void before() {

        Member admin = Member.builder()
            .userId("admin")
            .userPW("1234")
            .email("admin@naver.com")
            .role(Role.ROLE_ADMIN)
            .build();

        Member member = Member.builder()
            .userId("member")
            .userPW("1234")
            .email("member@naver.com")
            .role(Role.ROLE_USER)
            .build();

        Member savedAdmin = memberRepository.save(admin);
        Member savedMember = memberRepository.save(member);

        ChatRoom chatRoom = ChatRoom.builder()
            .admin(savedAdmin)
            .member(savedMember)
            .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        ChatMessage chatMessage = ChatMessage.builder()
            .chatRoom(savedChatRoom)
            .sender(savedMember)
            .message("hello")
            .build();
        chatMessageRepository.save(chatMessage);
    }

    @Test
    @DisplayName("회원 탈퇴 시 자식 엔티티 삭제")
    void deleteChild() {
        memberService.deleteMember("member");

        // todo: reservation 구현 후 삭제 여부 확인 필요
        Member member = memberRepository.findByUserIdAndDeleteDate("member", null).orElse(null);
        long chatRoomCount = chatRoomRepository.count();
        long chatMessageCount = chatMessageRepository.count();

        assertThat(member).isNull();
        assertThat(chatRoomCount).isEqualTo(0);
        assertThat(chatMessageCount).isEqualTo(0);
    }
}
