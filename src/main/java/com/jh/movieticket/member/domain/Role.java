package com.jh.movieticket.member.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 회원 권한 관련 enum
@Getter
@AllArgsConstructor
public enum Role {
    ROLE_ADMIN("ADMIN"), // 관리자
    ROLE_USER("USER"); // 회원

    private final String name;

    // Enum 검증을 위한 코드, Enum에 속하지 않으면 null 리턴
    @JsonCreator
    private static Role fromRole(String value) {

        return Arrays.stream(Role.values())
            .filter(r -> r.getName().equals(value.toUpperCase()))
            .findAny()
            .orElse(null);
    }
}
