package com.jh.movieticket.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 회원 권한 관련 enum
@Getter
@AllArgsConstructor
public enum Role {
    ROLE_ADMIN("ADMIN"), // 관리자
    ROLE_USER("USER"); // 회원

    private final String name;
}
