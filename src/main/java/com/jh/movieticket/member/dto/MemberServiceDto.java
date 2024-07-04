package com.jh.movieticket.member.dto;

import com.jh.movieticket.member.domain.Role;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 서비스 레이어 dto
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class MemberServiceDto implements Serializable {

    private Long id; // pk
    private String userId; // 아이디
    private String userPW; // 패스워드
    private String email; // 이메일
    private Role role; // 권한
    private LocalDateTime registerDate; // 생성 날짜
    private LocalDateTime changeDate; // 수정 날짜
    private LocalDateTime deleteDate; // 삭제 날짜

    /**
     * ServiceDto -> VerifyResponse
     *
     * @return VerifyResponse
     */
    public MemberVerifyDto.Response toVerifyResponse() {

        return MemberVerifyDto.Response.builder()
            .userId(userId)
            .userPw(userPW)
            .email(email)
            .build();
    }

    /**
     * ServiceDto -> ModifyResponse
     *
     * @return ModifyResponse
     */
    public MemberModifyDto.Response toModifyResponse() {

        return MemberModifyDto.Response.builder()
            .userId(userId)
            .userPw(userPW)
            .email(email)
            .build();
    }
}
