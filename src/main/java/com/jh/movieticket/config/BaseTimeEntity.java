package com.jh.movieticket.config;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime regDate; // 생성 날짜

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime chgDate; // 수정 날짜
}
