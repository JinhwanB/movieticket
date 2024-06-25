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
import com.jh.movieticket.member.dto.VerifyCodeDto;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MemberServiceTest {

    MemberService memberService;
    VerifyCodeDto.Request verifyCodeRequest;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    MailService mailService;

    @MockBean
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @BeforeEach
    void set(){

        memberService = new MemberService(memberRepository, passwordEncoder, mailService, redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        verifyCodeRequest = VerifyCodeDto.Request.builder()
            .email("test@gmail.com")
            .code("1234")
            .build();
    }

    @Test
    @DisplayName("이메일로 코드 발송")
    void sendEmailCode(){

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
    void sendEmailCodeFail(){

        String email = "test@gmail.com";

        when(memberRepository.existsByEmailAndDeleteDate(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> memberService.sendCode(email)).isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("입력받은 코드 확인")
    void verifyCode(){

        String code = "1234";

        when(redisTemplate.opsForValue().get(any())).thenReturn(code);

        assertThat(memberService.verifyCode(verifyCodeRequest)).isEqualTo(true);
    }

    @Test
    @DisplayName("입력받은 코드 확인 실패 - 올바르지 않은 입력")
    void verifyCodeFail(){

        String code = "1235";

        when(redisTemplate.opsForValue().get(any())).thenReturn(code);

        assertThat(memberService.verifyCode(verifyCodeRequest)).isEqualTo(false);
    }
}