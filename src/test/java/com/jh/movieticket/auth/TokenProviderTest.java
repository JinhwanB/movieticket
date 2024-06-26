package com.jh.movieticket.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.movieticket.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class TokenProviderTest {

    TokenProvider tokenProvider;

    @Value("${spring.jwt.secret}")
    String secretVal;

    @MockBean
    MemberService memberService;

    @BeforeEach
    void before() throws NoSuchFieldException, NoSuchMethodException {

        tokenProvider = new TokenProvider(memberService);

        // private 필드에 접근 및 값 변경
        Field secret = TokenProvider.class.getDeclaredField("secret");
        ReflectionUtils.setField(secret, tokenProvider, secretVal);
    }

    @Test
    @DisplayName("access 토큰 생성")
    void accessToken(){

        String userName = "test";
        List<String> roles = List.of("USER");

        String accessToken = tokenProvider.generateAccessToken(userName, roles);

        assertThat(accessToken).isNotNull();
    }

    @Test
    @DisplayName("refresh 토큰 생성")
    void refreshToken() {

        String userName = "test";
        List<String> roles = List.of("USER");
        MockHttpServletResponse response = new MockHttpServletResponse();

        tokenProvider.generateRefreshToken(userName, roles, response);

        Cookie cookie = Arrays.stream(response.getCookies())
            .filter(c -> c.getName().equals("refreshToken"))
            .findAny()
            .orElse(null);

        assertThat(cookie).isNotNull();
    }
}