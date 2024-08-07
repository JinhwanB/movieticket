package com.jh.movieticket.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.movieticket.auth.SecurityConfiguration;
import com.jh.movieticket.auth.TokenException;
import com.jh.movieticket.auth.TokenProvider;
import com.jh.movieticket.member.domain.Role;
import com.jh.movieticket.member.dto.MemberModifyDto;
import com.jh.movieticket.member.dto.MemberServiceDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(value = {MemberController.class, SecurityConfiguration.class})
class MemberControllerTest {

    String signUpRequest;
    VerifyCodeDto.Request verifyCodeRequest;
    MemberSignInDto.Request signInRequest;
    MemberModifyDto.Request modifyRequest;
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "registerDate"));
    MockMvc mockMvc;
    MemberServiceDto member;

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @MockBean
    TokenProvider tokenProvider;

    @BeforeEach
    void before() {

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"test@naver.com\",\n"
            + "    \"code\":\"3048\",\n"
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

        modifyRequest = MemberModifyDto.Request.builder()
            .userPw("12345")
            .email("test@gmail.com")
            .build();

        member = MemberServiceDto.builder()
            .userId("test")
            .userPW("1234")
            .email("test@naver.com")
            .role(Role.ROLE_USER)
            .build();
    }

    @Test
    @DisplayName("회원가입")
    void signUp() throws Exception {

        when(memberService.register(any())).thenReturn(member);

        mockMvc.perform(post("/members/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 아이디 작성 x")
    void signUpFail1() throws Exception {

        signUpRequest = "{\n"
            + "    \"userId\":\"\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"test@naver.com\",\n"
            + "    \"role\":\"user\"\n"
            + "}";

        mockMvc.perform(post("/members/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 작성 x")
    void signUpFail2() throws Exception {

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"\",\n"
            + "    \"email\":\"test@naver.com\",\n"
            + "    \"role\":\"user\"\n"
            + "}";

        mockMvc.perform(post("/members/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 작성 x")
    void signUpFail3() throws Exception {

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"\",\n"
            + "    \"role\":\"user\"\n"
            + "}";

        mockMvc.perform(post("/members/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 올바른 이메일이 아님")
    void signUpFail4() throws Exception {

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"testnaver.com\",\n"
            + "    \"role\":\"user\"\n"
            + "}";

        mockMvc.perform(post("/members/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 권한 작성 x 및 올바르지 않은 권한 작성")
    void signUpFail5() throws Exception {

        signUpRequest = "{\n"
            + "    \"userId\":\"test\",\n"
            + "    \"userPw\":\"1234\",\n"
            + "    \"email\":\"testnaver.com\",\n"
            + "    \"role\":\"\"\n"
            + "}";

        mockMvc.perform(post("/members/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 메일 발송")
    void sendEmail() throws Exception {

        String email = "test@naver.com";

        mockMvc.perform(post("/members/auth/" + email))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("성공"))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 메일 발송 실패 - 이메일 작성 x")
    void sendEmailFail1() throws Exception {

        String email = null;

        mockMvc.perform(post("/members/auth/" + email))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 메일 발송 실패 - 잘못된 이메일 작성 x")
    void sendEmailFail2() throws Exception {

        String email = "test";

        mockMvc.perform(post("/members/auth/" + email))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 확인")
    void verifyCode() throws Exception {

        mockMvc.perform(post("/members/auth/email")
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
    void verifyCodeFail1() throws Exception {

        VerifyCodeDto.Request badRequest = verifyCodeRequest.toBuilder()
            .email("")
            .build();

        mockMvc.perform(post("/members/auth/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 확인 실패 - 잘못된 이메일 입력")
    void verifyCodeFail2() throws Exception {

        VerifyCodeDto.Request badRequest = verifyCodeRequest.toBuilder()
            .email("test")
            .build();

        mockMvc.perform(post("/members/auth/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 확인 실패 - 인증코드 입력 x")
    void verifyCodeFail3() throws Exception {

        VerifyCodeDto.Request badRequest = verifyCodeRequest.toBuilder()
            .code("")
            .build();

        mockMvc.perform(post("/members/auth/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("인증코드 확인 실패 - 잘못된 인증코드 입력")
    void verifyCodeFail4() throws Exception {

        VerifyCodeDto.Request badRequest = verifyCodeRequest.toBuilder()
            .code("12345")
            .build();

        mockMvc.perform(post("/members/auth/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {

        when(memberService.login(any())).thenReturn(member);
        when(tokenProvider.generateAccessToken(any(), any())).thenReturn("jfdklsagjdl;safjkl");

        mockMvc.perform(post("/members/auth/login")
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
    void loginFail1() throws Exception {

        MemberSignInDto.Request badRequest = signInRequest.toBuilder()
            .userId("")
            .build();

        mockMvc.perform(post("/members/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 입력 x")
    void loginFail2() throws Exception {

        MemberSignInDto.Request badRequest = signInRequest.toBuilder()
            .userPw("")
            .build();

        mockMvc.perform(post("/members/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("로그아웃")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void logout() throws Exception {

        mockMvc.perform(post("/members/logout"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("성공"))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 실패 - 로그인 하지 않고 로그아웃 요청")
    void logoutFail1() throws Exception {

        mockMvc.perform(post("/members/logout"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 실패 - refresh 토큰 없음")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void logoutFail2() throws Exception {

        doThrow(TokenException.class).when(tokenProvider).logout(any(), any());

        mockMvc.perform(post("/members/logout"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("토큰 재발급")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void reGetToken() throws Exception {

        when(tokenProvider.reGenerateAccessToken(any(), any())).thenReturn(
            "jfdasklgjeiofjdslkgjfa");

        mockMvc.perform(post("/members/token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("성공"))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 로그인 x")
    void reGetTokenFail() throws Exception {

        mockMvc.perform(post("/members/token"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 수정")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void modify() throws Exception {

        String userId = "test";
        MemberServiceDto modifiedMember = member.toBuilder()
            .userId("ttt")
            .build();

        when(memberService.modifyMember(any(), any())).thenReturn(modifiedMember);

        mockMvc.perform(put("/members/member?userId=" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("성공"))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 수정할 회원 아이디 작성 x")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void modifyFail1() throws Exception {

        String userId = "";

        mockMvc.perform(put("/members/member?userId=" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 수정할 정보 중 비밀번호 작성 x")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void modifyFail2() throws Exception {

        String userId = "test";
        MemberModifyDto.Request badRequest = modifyRequest.toBuilder()
            .userPw("")
            .build();

        mockMvc.perform(put("/members/member?userId=" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 수정할 정보 중 이메일 작성 x")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void modifyFail3() throws Exception {

        String userId = "test";
        MemberModifyDto.Request badRequest = modifyRequest.toBuilder()
            .email("")
            .build();

        mockMvc.perform(put("/members/member?userId=" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 로그인 x")
    void modifyFail4() throws Exception {

        String userId = "test";

        mockMvc.perform(put("/members/member?userId=" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void deleteUser() throws Exception {

        String userId = "test";

        mockMvc.perform(delete("/members/member/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(204))
            .andExpect(jsonPath("$.message").value("성공"))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 탈퇴할 아이디 작성 x")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void deleteUserFail1() throws Exception {

        String userId = " ";

        mockMvc.perform(delete("/members/member/" + userId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 로그인 x")
    void deleteUserFail2() throws Exception {

        String userId = "test";

        mockMvc.perform(delete("/members/member/" + userId))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 조회")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void verify() throws Exception {

        String userId = "test";

        when(memberService.verifyMember(any())).thenReturn(member);

        mockMvc.perform(get("/members/member/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("성공"))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 조회 실패 - 조회할 회원 아이디 작성 x")
    @WithMockUser(username = "test", roles = {"USER", "ADMIN"})
    void verifyFail1() throws Exception {

        String userId = " ";

        mockMvc.perform(get("/members/member/" + userId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].status").value(400))
            .andExpect(jsonPath("$[0].data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 조회 실패 - 로그인 x")
    void verifyFail2() throws Exception {

        String userId = "test";

        mockMvc.perform(get("/members/member/" + userId))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 전체 리스트 조회")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void all() throws Exception {

        MemberServiceDto admin = MemberServiceDto.builder()
            .userId("admin")
            .userPW("12345")
            .email("admin@gmail.com")
            .build();
        List<MemberServiceDto> members = List.of(member, admin);
        Page<MemberServiceDto> memberList = new PageImpl<>(members, pageable, members.size());

        when(memberService.allMembers(any())).thenReturn(memberList);

        mockMvc.perform(get("/members"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("성공"))
            .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("회원 전체 리스트 조회 실패 - 로그인 x")
    void allFail1() throws Exception {

        mockMvc.perform(get("/members"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("회원 전체 리스트 조회 실패 - 권한 없음")
    @WithMockUser(username = "test")
    void allFail2() throws Exception {

        mockMvc.perform(get("/members"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.data").doesNotExist());
    }
}