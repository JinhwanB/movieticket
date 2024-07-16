package com.jh.movieticket.theater.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.movieticket.config.JpaAuditingConfig;
import com.jh.movieticket.theater.domain.Seat;
import com.jh.movieticket.theater.domain.Theater;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class TheaterRepositoryTest {

    Pageable pageable = PageRequest.of(0, 10, Direction.ASC, "name");

    @Autowired
    TheaterRepository theaterRepository;

    @BeforeEach
    void before() {

        Theater theater1 = Theater.builder()
            .name("test")
            .seatList(new ArrayList<>())
            .build();
        Theater theater2 = Theater.builder()
            .name("table")
            .seatList(new ArrayList<>())
            .build();
        theaterRepository.save(theater1);
        Theater saved = theaterRepository.save(theater2);

        Seat seat = Seat.builder()
            .theater(saved)
            .seatNo(1)
            .build();
        saved.getSeatList().add(seat);
        theaterRepository.save(saved);
    }

    @Test
    @DisplayName("중복된 이름 확인")
    void duplicatedOfTheater() {

        boolean isDuplicated = theaterRepository.existsByNameAndDeleteDate("test", null);

        assertThat(isDuplicated).isEqualTo(true);
    }

    @Test
    @DisplayName("삭제되지 않은 상영관 중 상영관 이름으로 조회")
    void findOneOfTheater() {

        Theater theater = theaterRepository.findByNameAndDeleteDate("test", null).orElse(null);

        assertThat(theater.getName()).isEqualTo("test");
    }

    @Test
    @DisplayName("삭제되지 않은 상영관 전체 리스트 조회")
    void findALlOfTheater() {

        Page<Theater> theaters = theaterRepository.findAllByDeleteDateIsNull(pageable);

        assertThat(theaters.getTotalElements()).isEqualTo(2);
        assertThat(theaters.getContent().get(0).getName()).isEqualTo("table");
        assertThat(theaters.getContent().get(0).getSeatList().get(0).getSeatNo()).isEqualTo(1);
    }
}