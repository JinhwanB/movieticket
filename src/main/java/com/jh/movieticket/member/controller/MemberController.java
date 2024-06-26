package com.jh.movieticket.member.controller;

import com.jh.movieticket.auth.TokenException;
import com.jh.movieticket.auth.TokenProvider;
import com.jh.movieticket.config.GlobalApiResponse;
import com.jh.movieticket.member.dto.MemberSignInDto;
import com.jh.movieticket.member.dto.MemberSignUpDto;
import com.jh.movieticket.member.dto.VerifyCodeDto;
import com.jh.movieticket.member.exception.MemberErrorCode;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    /**
     * 인증코드 메일 발송
     *
     * @param email 메일 받을 이메일
     * @return 메일 전송 여부
     */
    @PostMapping("/auth/{email}")
    public ResponseEntity<GlobalApiResponse<?>> sendEmailCode(
        @NotBlank(message = "이메일을 입력해주세요.") @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "올바른 이메일을 입력해주세요.") @PathVariable String email) {

        memberService.sendCode(email);

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(null));
    }

    /**
     * 인증코드 확인
     *
     * @param request 인증코드 받은 이메일과 입력한 인증코드
     * @return 인증코드 매칭 여부
     */
    @PostMapping("/auth/email")
    public ResponseEntity<GlobalApiResponse<?>> verifyEmailCode(
        @Valid @RequestBody VerifyCodeDto.Request request) {

        memberService.verifyCode(request);

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(null));
    }

    /**
     * 회원가입
     *
     * @param request 회원가입을 위한 정보
     * @return 응답 성공 -> 200 코드와 회원 아이디, 응답 실패 -> 실패 코드와 에러 메시지
     */
    @PostMapping("/auth/signup")
    public ResponseEntity<GlobalApiResponse<String>> signUp(
        @Valid @RequestBody MemberSignUpDto.Request request) {

        String userId = memberService.register(request);

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(userId));
    }

    /**
     * 로그인
     *
     * @param request  아이디와 비밀번호
     * @param response HttpServletResponse
     * @return 로그인 성공 -> 200코드와 access 토큰, 실패 -> 에러코드와 에러메시지
     */
    @PostMapping("/auth/login")
    public ResponseEntity<GlobalApiResponse<String>> login(
        @Valid @RequestBody MemberSignInDto.Request request, HttpServletResponse response) {

        MemberSignInDto.Response signInDto = memberService.login(request);
        String userId = signInDto.getUserId();
        List<String> role = signInDto.getRole();

        String accessToken = tokenProvider.generateAccessToken(userId, role);
        tokenProvider.generateRefreshToken(userId, role, response);

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(accessToken));
    }

    /**
     * 로그아웃
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 로그아웃 성공 -> 200코드와 성공메시지, 로그아웃 실패 -> 에러코드와 에러메시지
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<GlobalApiResponse<?>> logout(HttpServletRequest request,
        HttpServletResponse response) {

        try {
            tokenProvider.logout(request, response);
        } catch (TokenException e) { // refresh 토큰이 없다면 이미 로그아웃 한 것으로 처리
            throw new MemberException(MemberErrorCode.ALREADY_LOGOUT);
        }

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(null));
    }
}
