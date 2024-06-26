package com.jh.movieticket.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.movieticket.auth.TokenProvider;
import com.jh.movieticket.member.domain.Role;
import com.jh.movieticket.member.dto.MemberSignInDto;
import com.jh.movieticket.member.dto.VerifyCodeDto;
import com.jh.movieticket.member.service.MemberService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    String signUpRequest;
    VerifyCodeDto.Request verifyCodeRequest;
    MemberSignInDto.Request signInRequest;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @MockBean
    TokenProvider tokenProvider;

    @BeforeEach
    void before() {

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"test@naver.com\",\n"
            + "    \"role\":\"user\"\n"
            + "}";

        verifyCodeRequest = VerifyCodeDto.Request.builder()
            .email("test@naver.com")
            .code("1234")
            .build();

        signInRequest = MemberSignInDto.Request.builder()
            .userId("test")
            .userPw("1234")
            .build();
    }

    @Test
    @DisplayName("회원가입")
    @WithMockUser(username = "test")
    void signUp() throws Exception {

        when(memberService.register(any())).thenReturn("test");

        mockMvc.perform(post("/members/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 아이디 작성 x")
    @WithMockUser(username = "test")
    void signUpFail1() throws Exception {

        signUpRequest = "{\n"
            + "    \"userId\":\"\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"test@naver.com\",\n"
            + "    \"role\":\"user\"\n"
            + "}";

        mockMvc.perform(post("/members/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 작성 x")
    @WithMockUser(username = "test")
    void signUpFail2() throws Exception {

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"\",\n"
            + "    \"email\":\"test@naver.com\",\n"
            + "    \"role\":\"user\"\n"
            + "}";

        mockMvc.perform(post("/members/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 작성 x")
    @WithMockUser(username = "test")
    void signUpFail3() throws Exception {

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"\",\n"
            + "    \"role\":\"user\"\n"
            + "}";

        mockMvc.perform(post("/members/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 올바른 이메일이 아님")
    @WithMockUser(username = "test")
    void signUpFail4() throws Exception {

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"testnaver.com\",\n"
            + "    \"role\":\"user\"\n"
            + "}";

        mockMvc.perform(post("/members/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 권한 작성 x 및 올바르지 않은 권한 작성")
    @WithMockUser(username = "test")
    void signUpFail5() throws Exception {

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"testnaver.com\",\n"
            + "    \"role\":\"\"\n"
            + "}";

        mockMvc.perform(post("/members/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 메일 발송")
    @WithMockUser(username = "test")
    void sendEmail() throws Exception {

        String email = "test@naver.com";

        mockMvc.perform(post("/members/auth/" + email)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("성공"))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 메일 발송 실패 - 이메일 작성 x")
    @WithMockUser(username = "test")
    void sendEmailFail1() throws Exception {

        String email = null;

        mockMvc.perform(post("/members/auth/" + email)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 메일 발송 실패 - 잘못된 이메일 작성 x")
    @WithMockUser(username = "test")
    void sendEmailFail2() throws Exception {

        String email = "test";

        mockMvc.perform(post("/members/auth/" + email)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 확인")
    @WithMockUser(username = "test")
    void verifyCode() throws Exception {

        mockMvc.perform(post("/members/auth/email")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyCodeRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("성공"))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 확인 실패 - 이메일 입력 x")
    @WithMockUser(username = "test")
    void verifyCodeFail1() throws Exception {

        VerifyCodeDto.Request badRequest = verifyCodeRequest.toBuilder()
            .email("")
            .build();

        mockMvc.perform(post("/members/auth/email")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 확인 실패 - 잘못된 이메일 입력")
    @WithMockUser(username = "test")
    void verifyCodeFail2() throws Exception {

        VerifyCodeDto.Request badRequest = verifyCodeRequest.toBuilder()
            .email("test")
            .build();

        mockMvc.perform(post("/members/auth/email")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 확인 실패 - 인증코드 입력 x")
    @WithMockUser(username = "test")
    void verifyCodeFail3() throws Exception {

        VerifyCodeDto.Request badRequest = verifyCodeRequest.toBuilder()
            .code("")
            .build();

        mockMvc.perform(post("/members/auth/email")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 확인 실패 - 잘못된 인증코드 입력")
    @WithMockUser(username = "test")
    void verifyCodeFail4() throws Exception {

        VerifyCodeDto.Request badRequest = verifyCodeRequest.toBuilder()
            .code("12345")
            .build();

        mockMvc.perform(post("/members/auth/email")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("로그인")
    @WithMockUser(username = "test")
    void login() throws Exception {

        MemberSignInDto.Response response = MemberSignInDto.Response.builder()
            .userId("test")
            .role(List.of(Role.ROLE_USER.getName()))
            .build();

        when(memberService.login(any())).thenReturn(response);
        when(tokenProvider.generateAccessToken(any(), any())).thenReturn("jfdklsagjdl;safjkl");

        mockMvc.perform(post("/members/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("성공"))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 아이디 입력 x")
    @WithMockUser(username = "test")
    void loginFail1() throws Exception {

        MemberSignInDto.Request badRequest = signInRequest.toBuilder()
            .userId("")
            .build();

        mockMvc.perform(post("/members/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 입력 x")
    @WithMockUser(username = "test")
    void loginFail2() throws Exception {

        MemberSignInDto.Request badRequest = signInRequest.toBuilder()
            .userPw("")
            .build();

        mockMvc.perform(post("/members/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }
}