package com.jh.movieticket.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원 조회를 위한 dto
public class MemberVerifyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response{

        private String userId; // 아이디
        private String userPw; // 비밀번호
        private String email; // 이메일
    }
}
