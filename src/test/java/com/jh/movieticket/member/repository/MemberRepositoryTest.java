package com.jh.movieticket.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.movieticket.config.JpaAuditingConfig;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void before(){

        Member member = Member.builder()
            .userId("test")
            .userPW("1234")
            .email("test@naver.com")
            .role(Role.ROLE_USER)
            .build();

        Member admin = Member.builder()
            .userId("admin")
            .userPW("1234")
            .email("admin@gmail.com")
            .role(Role.ROLE_ADMIN)
            .build();

        memberRepository.save(member);
        memberRepository.save(admin);
    }

    @Test
    @DisplayName("탈퇴하지 않은 회원 중 회원 아이디를 통해 조회")
    void findMemberById(){

        Member member = memberRepository.findByUserIdAndDeleteDate("test", null).orElse(null);

        assertThat(member.getUserId()).isEqualTo("test");
    }

    @Test
    @DisplayName("중복 아이디 확인")
    void duplicatedId(){

        assertThat(memberRepository.existsByUserIdAndDeleteDate("test", null)).isEqualTo(true);
    }

    @Test
    @DisplayName("중복 이메일 확인")
    void duplicatedEmail(){

        assertThat(memberRepository.existsByEmailAndDeleteDate("test@naver.com", null)).isEqualTo(true);
    }
}