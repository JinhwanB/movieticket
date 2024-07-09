package com.jh.movieticket.member.domain;

import com.jh.movieticket.config.BaseTimeEntity;
import com.jh.movieticket.member.dto.MemberServiceDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Member extends BaseTimeEntity {

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
     * Entity -> ServiceDto
     *
     * @return ServiceDto
     */
    public MemberServiceDto toServiceDto() {

        return MemberServiceDto.builder()
            .id(id)
            .userId(userId)
            .userPW(userPW)
            .role(role)
            .email(email)
            .registerDate(getRegisterDate())
            .changeDate(getChangeDate())
            .deleteDate(deleteDate)
            .build();
    }
}
