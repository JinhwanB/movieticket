package com.jh.movieticket.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.movieticket.chat.domain.ChatRoom;
import com.jh.movieticket.config.JpaAuditingConfig;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.domain.Role;
import com.jh.movieticket.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class ChatRoomRepositoryTest {

    Member savedMember;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void before() {

        Member admin = Member.builder()
            .userId("admin")
            .userPW("1234")
            .role(Role.ROLE_ADMIN)
            .email("admin@naver.com")
            .build();

        Member member = Member.builder()
            .userId("member")
            .role(Role.ROLE_USER)
            .email("member@naver.com")
            .userPW("1234")
            .build();
        Member savedAdmin = memberRepository.save(admin);
        savedMember = memberRepository.save(member);

        ChatRoom chatRoom = ChatRoom.builder()
            .admin(savedAdmin)
            .member(savedMember)
            .build();
        chatRoomRepository.save(chatRoom);
    }

    @Test
    @DisplayName("회원을 통해 채팅방 조회")
    void verifyByMember() {

        ChatRoom chatRoom = chatRoomRepository.findByMember(savedMember)
            .orElse(null);

        assertThat(chatRoom.getMember().getUserId()).isEqualTo("member");
    }
}