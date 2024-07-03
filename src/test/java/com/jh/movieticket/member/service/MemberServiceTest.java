package com.jh.movieticket.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jh.movieticket.mail.service.MailService;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.domain.Role;
import com.jh.movieticket.member.dto.MemberModifyDto;
import com.jh.movieticket.member.dto.MemberSignInDto;
import com.jh.movieticket.member.dto.MemberSignUpDto;
import com.jh.movieticket.member.dto.VerifyCodeDto;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MemberServiceTest {

    MemberService memberService;
    VerifyCodeDto.Request verifyCodeRequest;
    MemberSignUpDto.Request signUpRequest;
    MemberSignInDto.Request signInRequest;
    MemberModifyDto.Request modifyRequest;
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "registerDate"));
    Member member;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    MailService mailService;

    @MockBean
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void set() {

        memberService = new MemberService(memberRepository, passwordEncoder, mailService,
            redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        verifyCodeRequest = VerifyCodeDto.Request.builder()
            .email("test@gmail.com")
            .code("1234")
            .build();

        signUpRequest = MemberSignUpDto.Request.builder()
            .userId("test")
            .userPw("1234")
            .email("test@gmail.com")
            .role(Role.ROLE_USER)
            .build();

        signInRequest = MemberSignInDto.Request.builder()
            .userId("test")
            .userPw("1234")
            .build();

        modifyRequest = MemberModifyDto.Request.builder()
            .userPw("2345")
            .email("test@gmail.com")
            .build();

        member = Member.builder()
            .userId("test")
            .userPW("1234")
            .email("test@naver.com")
            .role(Role.ROLE_USER)
            .build();
    }

    @Test
    @DisplayName("이메일로 코드 발송")
    void sendEmailCode() {

        String email = "test@gmail.com";
        String code = "1234";

        when(memberRepository.existsByEmailAndDeleteDate(any(), any())).thenReturn(false);
        doNothing().when(mailService).sendEmail(any(), any(), any());
        doNothing().when(valueOperations).set(email, code);
        when(redisTemplate.opsForValue().get(email)).thenReturn(code);

        memberService.sendCode(email);

        verify(mailService, times(1)).sendEmail(eq(email), any(), any());
        verify(valueOperations, times(1)).set(eq(email), any());
        assertThat(redisTemplate.opsForValue().get(email)).isEqualTo(code);
    }

    @Test
    @DisplayName("이메일로 코드 발송 실패 - 중복된 이메일")
    void sendEmailCodeFail() {

        String email = "test@gmail.com";

        when(memberRepository.existsByEmailAndDeleteDate(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> memberService.sendCode(email)).isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("입력받은 코드 확인")
    void verifyCode() {

        String code = "1234";

        when(redisTemplate.opsForValue().get(any())).thenReturn(code);

        memberService.verifyCode(verifyCodeRequest);

        verify(redisTemplate, times(1)).delete(verifyCodeRequest.getEmail());
    }

    @Test
    @DisplayName("입력받은 코드 확인 실패 - 올바르지 않은 입력")
    void verifyCodeFail() {

        String code = "1235";

        when(redisTemplate.opsForValue().get(any())).thenReturn(code);

        assertThatThrownBy(() -> memberService.verifyCode(verifyCodeRequest)).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("회원가입")
    void register() {

        when(memberRepository.existsByUserIdAndDeleteDate(any(), any())).thenReturn(false);
        when(memberRepository.existsByEmailAndDeleteDate(any(), any())).thenReturn(false);
        when(memberRepository.save(any())).thenReturn(member);
        when(redisTemplate.hasKey(any())).thenReturn(true);

        Member registered = memberService.register(signUpRequest);

        assertThat(registered.getUserId()).isEqualTo(signUpRequest.getUserId());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 아이디")
    void registerFail1() {

        when(memberRepository.existsByUserIdAndDeleteDate(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> memberService.register(signUpRequest)).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 이메일")
    void registerFail2() {

        when(memberRepository.existsByUserIdAndDeleteDate(any(), any())).thenReturn(false);
        when(memberRepository.existsByEmailAndDeleteDate(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> memberService.register(signUpRequest)).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("로그인")
    void login() {

        Member member = Member.builder()
            .id(1L)
            .userId("test")
            .userPW("1234")
            .role(Role.ROLE_USER)
            .email("test@naver.com")
            .build();

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        Member signInMember = memberService.login(signInRequest);

        assertThat(signInMember.getUserId()).isEqualTo("test");
    }

    @Test
    @DisplayName("로그인 실패 - 없는 회원")
    void loginFail1() {

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.login(signInRequest)).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 다름")
    void loginFail2() {

        Member member = Member.builder()
            .id(1L)
            .userId("test")
            .userPW("1235")
            .role(Role.ROLE_USER)
            .email("test@naver.com")
            .build();

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> memberService.login(signInRequest)).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("회원 조회")
    void verifyMember() {

        Member member = Member.builder()
            .id(1L)
            .userId("test")
            .userPW("1234")
            .role(Role.ROLE_USER)
            .email("test@naver.com")
            .build();

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));

        Member verifiedMember = memberService.verifyMember("test");

        assertThat(verifiedMember.getUserId()).isEqualTo("test");
    }

    @Test
    @DisplayName("회원 조회 실패 - 없는 회원")
    void verifyMemberFail() {

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.verifyMember("test")).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("회원 정보 수정")
    void modifyMember() {

        Member modified = member.toBuilder()
            .userPW(modifyRequest.getUserPw())
            .email(modifyRequest.getEmail())
            .build();

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));
        when(memberRepository.existsByUserIdAndDeleteDate(any(), any())).thenReturn(false);
        when(memberRepository.existsByEmailAndDeleteDate(any(), any())).thenReturn(false);
        when(redisTemplate.hasKey(any())).thenReturn(true);
        when(memberRepository.save(any())).thenReturn(modified);

        Member modifiedMember = memberService.modifyMember("test", modifyRequest);

        assertThat(modifiedMember.getEmail()).isEqualTo(modifyRequest.getEmail());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 없는 회원")
    void modifyMemberFail1() {

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.modifyMember("test", modifyRequest)).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 중복된 이메일")
    void modifyMemberFail3() {

        Member member = Member.builder()
            .id(1L)
            .userId("test")
            .userPW("1234")
            .email("test@naver.com")
            .role(Role.ROLE_USER)
            .build();

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));
        when(memberRepository.existsByUserIdAndDeleteDate(any(), any())).thenReturn(false);
        when(memberRepository.existsByEmailAndDeleteDate(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> memberService.modifyMember("test", modifyRequest)).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("회원 탈퇴")
    void delete() {

        Member member = Member.builder()
            .id(1L)
            .userId("test")
            .userPW("1234")
            .email("test@naver.com")
            .role(Role.ROLE_USER)
            .deleteDate(null)
            .build();

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.of(member));

        memberService.deleteMember("test");

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository, times(1)).save(memberCaptor.capture());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 없는 회원")
    void deleteFail() {

        when(memberRepository.findByUserIdAndDeleteDate(any(), any())).thenReturn(
            Optional.empty());

        assertThatThrownBy(() -> memberService.deleteMember("test")).isInstanceOf(
            MemberException.class);
    }

    @Test
    @DisplayName("회원 전체 리스트 조회")
    void memberList() {

        Member member = Member.builder()
            .userId("test")
            .userPW("1234")
            .email("test@naver.com")
            .id(1L)
            .build();
        List<Member> members = List.of(member);
        Page<Member> memberList = new PageImpl<>(members, pageable, members.size());

        when(memberRepository.findAllByDeleteDate(any(), any())).thenReturn(memberList);

        Page<Member> allMembers = memberService.allMembers(pageable);

        assertThat(allMembers.getNumberOfElements()).isEqualTo(1);
        assertThat(allMembers.getContent().get(0).getUserId()).isEqualTo("test");
    }
}