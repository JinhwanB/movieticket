package com.jh.movieticket.member.dto;

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
    private LocalDateTime registerDate; // 생성 날짜
    private LocalDateTime changeDate; // 수정 날짜
    private LocalDateTime deleteDate; // 삭제 날짜
}
