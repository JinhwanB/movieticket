package com.jh.movieticket.member.controller;

import com.jh.movieticket.auth.TokenProvider;
import com.jh.movieticket.config.GlobalApiResponse;
import com.jh.movieticket.member.dto.MemberSignUpDto;
import com.jh.movieticket.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

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

//    @PostMapping("/auth/login")
//    public ResponseEntity<GlobalApiResponse<String>> login(@Valid @RequestBody MemberSignInDto.Request request, HttpServletResponse response){
//
//        MemberSignInDto.Response signInDto = memberService.login(request);
//        String userId = signInDto.getUserId();
//        List<String> role = signInDto.getRole();
//
//        String accessToken = tokenProvider.generateAccessToken(userId, role);
//        String refreshToken = tokenProvider.generateRefreshToken(userId);
//        tokenToCookie(refreshToken, response);
//
//        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(accessToken));
//    }
}
