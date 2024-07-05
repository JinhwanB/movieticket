package com.jh.movieticket.theater.repository;

import com.jh.movieticket.theater.domain.Theater;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {

    boolean existsByNameAndDeleteDate(String name, LocalDateTime deleteDate); // 상영관 이름 중복 여부 확인 메소드
}
