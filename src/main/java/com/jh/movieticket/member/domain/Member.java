package com.jh.movieticket.member.domain;

import com.jh.movieticket.config.BaseTimeEntity;
import com.jh.movieticket.member.dto.MemberModifyDto;
import com.jh.movieticket.member.dto.MemberVerifyDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class Member extends BaseTimeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk

    @Column(nullable = false)
    private String userId; // 아이디

    @Column(nullable = false)
    private String userPW; // 패스워드

    @Column(nullable = false)
    private String email; // 이메일

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role; // 권한

    @Column
    private LocalDateTime deleteDate; // 삭제날짜

    /**
     * Entity -> VerifyResponse
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
     * Entity -> ModifyResponse
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
