package com.jh.movieticket.member.dto;

import com.jh.movieticket.member.domain.Role;
import com.jh.movieticket.validation.IsEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원가입용 dto
public class MemberSignUpDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Request{

        @NotBlank(message = "아이디를 입력해주세요.")
        private String memberId; // 아이디

        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String memberPw; // 비밀번호

        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "올바른 이메일을 입력해주세요.")
        private String email; // 이메일

        @IsEnum(message = "올바른 권한을 입력해주세요.")
        private Role role; // 권한
    }
}