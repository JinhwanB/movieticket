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
import com.jh.movieticket.member.service.MemberService;
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

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @MockBean
    TokenProvider tokenProvider;

    @BeforeEach
    void before(){

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"test@naver.com\",\n"
            + "    \"role\":\"user\"\n"
            + "}";
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
}