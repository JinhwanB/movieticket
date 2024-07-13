package com.jh.movieticket.theater.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jh.movieticket.movie.domain.MovieSchedule;
import com.jh.movieticket.movie.repository.MovieScheduleRepository;
import com.jh.movieticket.theater.domain.Theater;
import com.jh.movieticket.theater.dto.TheaterCreateDto;
import com.jh.movieticket.theater.dto.TheaterModifyDto;
import com.jh.movieticket.theater.dto.TheaterServiceDto;
import com.jh.movieticket.theater.exception.TheaterException;
import com.jh.movieticket.theater.repository.TheaterRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class TheaterServiceTest {

    TheaterService theaterService;
    TheaterCreateDto.Request createRequest;
    TheaterModifyDto.Request modifyRequest;

    @MockBean
    TheaterRepository theaterRepository;

    @MockBean
    MovieScheduleRepository movieScheduleRepository;

    @BeforeEach
    void before() {
        theaterService = new TheaterService(theaterRepository, movieScheduleRepository);

        createRequest = TheaterCreateDto.Request.builder()
            .name("test")
            .seatCnt(10)
            .build();

        modifyRequest = TheaterModifyDto.Request.builder()
            .originName("test")
            .changedName("table")
            .build();
    }

    @Test
    @DisplayName("상영관 생성 서비스")
    void createTheater() {

        Theater save = Theater.builder()
            .name("test")
            .build();
        Theater theater = Theater.builder()
            .name("test")
            .seatList(new ArrayList<>())
            .build();

        when(theaterRepository.existsByNameAndDeleteDate(any(), any())).thenReturn(false);
        when(theaterRepository.save(any())).thenReturn(save);
        when(theaterRepository.save(any())).thenReturn(theater);

        TheaterServiceDto createdTheater = theaterService.createTheater(createRequest);

        assertThat(createdTheater.getName()).isEqualTo("test");
    }

    @Test
    @DisplayName("상영관 생성 서비스 실패 - 중복된 이름")
    void createTheaterFail() {

        when(theaterRepository.existsByNameAndDeleteDate(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> theaterService.createTheater(createRequest)).isInstanceOf(
            TheaterException.class);
    }

    @Test
    @DisplayName("상영관 이름 수정 서비스")
    void updateTheater() {

        Theater theater = Theater.builder()
            .name("test")
            .seatList(new ArrayList<>())
            .build();
        Theater changed = theater.toBuilder()
            .name("table")
            .build();

        when(theaterRepository.findByNameAndDeleteDate(any(), any())).thenReturn(
            Optional.of(theater));
        when(theaterRepository.existsByNameAndDeleteDate(any(), any())).thenReturn(false);
        when(theaterRepository.save(any())).thenReturn(changed);

        TheaterServiceDto changedTheater = theaterService.updateTheater(modifyRequest);
        assertThat(changedTheater.getName()).isEqualTo("table");
    }

    @Test
    @DisplayName("상영관 이름 수정 서비스 실패 - 없는 상영관")
    void updateTheaterFail1() {

        when(theaterRepository.findByNameAndDeleteDate(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> theaterService.updateTheater(modifyRequest)).isInstanceOf(
            TheaterException.class);
    }

    @Test
    @DisplayName("상영관 이름 수정 서비스 실패 - 중복된 이름")
    void updateTheaterFail2() {

        Theater theater = Theater.builder()
            .name("test")
            .seatList(new ArrayList<>())
            .build();

        when(theaterRepository.findByNameAndDeleteDate(any(), any())).thenReturn(
            Optional.of(theater));
        when(theaterRepository.existsByNameAndDeleteDate(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> theaterService.updateTheater(modifyRequest)).isInstanceOf(
            TheaterException.class);
    }

    @Test
    @DisplayName("상영관 삭제 서비스")
    void deleteTheater() {

        Theater theater = Theater.builder()
            .name("test")
            .seatList(new ArrayList<>())
            .build();
        List<MovieSchedule> movieScheduleList = new ArrayList<>();

        when(theaterRepository.findByNameAndDeleteDate(any(), any())).thenReturn(
            Optional.of(theater));
        when(movieScheduleRepository.findByTheaterId(any())).thenReturn(movieScheduleList);

        theaterService.deleteTheater("test");

        verify(theaterRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("상영관 삭제 서비스 실패 - 없는 상영관")
    void deleteTheaterFail1() {

        when(theaterRepository.findByNameAndDeleteDate(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> theaterService.deleteTheater("test")).isInstanceOf(
            TheaterException.class);
    }

    @Test
    @DisplayName("상영관 삭제 서비스 실패 - 영화 스케줄 존재")
    void deleteTheaterFail2() {

        Theater theater = Theater.builder()
            .name("test")
            .seatList(new ArrayList<>())
            .build();
        List<MovieSchedule> movieScheduleList = new ArrayList<>();
        MovieSchedule movieSchedule = MovieSchedule.builder()
            .theater(theater)
            .build();
        movieScheduleList.add(movieSchedule);

        when(theaterRepository.findByNameAndDeleteDate(any(), any())).thenReturn(
            Optional.of(theater));
        when(movieScheduleRepository.findByTheaterId(any())).thenReturn(movieScheduleList);

        assertThatThrownBy(() -> theaterService.deleteTheater("test")).isInstanceOf(
            TheaterException.class);
    }

    @Test
    @DisplayName("상영관 조회 서비스")
    void verifyTheater() {

        Theater theater = Theater.builder()
            .name("test")
            .seatList(new ArrayList<>())
            .build();

        when(theaterRepository.findByNameAndDeleteDate(any(), any())).thenReturn(
            Optional.of(theater));

        TheaterServiceDto theaterServiceDto = theaterService.verify("test");
        assertThat(theaterServiceDto.getName()).isEqualTo("test");
    }

    @Test
    @DisplayName("상영관 조회 서비스 실패 - 없는 상영관")
    void verifyTheaterFail() {

        when(theaterRepository.findByNameAndDeleteDate(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> theaterService.verify("test")).isInstanceOf(
            TheaterException.class);
    }

    @Test
    @DisplayName("상영관 전체 리스트 조회 서비스")
    void verifyAllOfTheater() {

        Theater theater1 = Theater.builder()
            .name("test")
            .seatList(new ArrayList<>())
            .build();
        Theater theater2 = theater1.toBuilder()
            .name("table")
            .build();
        List<Theater> theaterList = List.of(theater1, theater2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "name"));
        Page<Theater> pagingList = new PageImpl<>(theaterList, pageable, theaterList.size());

        when(theaterRepository.findAllByDeleteDateIsNull(any())).thenReturn(pagingList);

        Page<TheaterServiceDto> theaterServiceDtos = theaterService.verifyAll(pageable);

        assertThat(theaterServiceDtos.getTotalElements()).isEqualTo(2);
    }
}