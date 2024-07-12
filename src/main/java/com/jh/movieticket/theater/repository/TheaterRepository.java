package com.jh.movieticket.theater.repository;

import com.jh.movieticket.theater.domain.Theater;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {

    boolean existsByNameAndDeleteDate(String name, LocalDateTime deleteDate); // 상영관 이름 중복 여부 확인 메소드

    Optional<Theater> findByNameAndDeleteDate(String name,
        LocalDateTime deleteDate); // 상영관 이름으로 상영관 조회

    @Query("select distinct t from Theater t left join fetch t.seatList where t.deleteDate is null")
    List<Theater> findAllWithFetchJoin();
}
