package com.jh.movieticket.theater.service;

import com.jh.movieticket.config.CacheName;
import com.jh.movieticket.movie.domain.MovieSchedule;
import com.jh.movieticket.movie.repository.MovieScheduleRepository;
import com.jh.movieticket.theater.domain.Seat;
import com.jh.movieticket.theater.domain.Theater;
import com.jh.movieticket.theater.dto.TheaterCreateDto;
import com.jh.movieticket.theater.dto.TheaterServiceDto;
import com.jh.movieticket.theater.exception.TheaterErrorCode;
import com.jh.movieticket.theater.exception.TheaterException;
import com.jh.movieticket.theater.repository.TheaterRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final MovieScheduleRepository movieScheduleRepository;

    /**
     * 상영관을 생성하고 좌석도 함께 생성한다.
     *
     * @param theaterCreateDto 생성할 상영관 정보
     * @return 생성한 상영관 정보
     */
    public TheaterServiceDto createTheater(TheaterCreateDto.Request theaterCreateDto) {

        String name = theaterCreateDto.getName();
        int seatCnt = theaterCreateDto.getSeatCnt();

        // 상영관 이름이 중복되는 경우
        if (theaterRepository.existsByNameAndDeleteDate(name, null)) {
            throw new TheaterException(TheaterErrorCode.EXIST_NAME);
        }

        Theater theater = Theater.builder()
            .name(name)
            .build();

        Theater save = theaterRepository.save(theater);

        // 좌석 함께 저장
        List<Seat> seatList = new ArrayList<>();
        IntStream.range(1, seatCnt + 1)
            .forEach(i -> {
                Seat seat = Seat.builder()
                    .seatNo(i)
                    .theater(save)
                    .build();
                seatList.add(seat);
            });

        Theater theaterWithSeat = save.toBuilder()
            .seatList(seatList)
            .build();

        Theater savedTheater = theaterRepository.save(theaterWithSeat);

        return savedTheater.toServiceDto();
    }

    /**
     * 상영관 이름 수정 서비스
     *
     * @param originName  수정할 상영관 이름
     * @param changedName 수정하고자 하는 상영관 이름
     * @return 수정된 상영관 정보
     */
    @CacheEvict(key = "#originName", value = CacheName.THEATER_CACHE_NAME)
    @CachePut(key = "#changedName", value = CacheName.THEATER_CACHE_NAME)
    public TheaterServiceDto updateTheater(String originName, String changedName) {

        Theater theater = theaterRepository.findByNameAndDeleteDate(originName, null)
            .orElseThrow(() -> new TheaterException(TheaterErrorCode.NOT_FOUND_THEATER));

        // 바꾸려는 이름이 중복되는 경우
        if (theaterRepository.existsByNameAndDeleteDate(changedName, null)) {
            throw new TheaterException(TheaterErrorCode.EXIST_NAME);
        }

        Theater changedTheater = theater.toBuilder()
            .name(changedName)
            .build();
        Theater changed = theaterRepository.save(changedTheater);

        return changed.toServiceDto();
    }

    /**
     * 상영관 삭제 서비스
     *
     * @param name 삭제할 상영관 이름
     */
    @CacheEvict(key = "#name", value = CacheName.THEATER_CACHE_NAME)
    public void deleteTheater(String name) {

        Theater theater = theaterRepository.findByNameAndDeleteDate(name, null)
            .orElseThrow(() -> new TheaterException(TheaterErrorCode.NOT_FOUND_THEATER));

        List<MovieSchedule> movieScheduleList = movieScheduleRepository.findByTheaterId(
            theater.getId());

        if (!movieScheduleList.isEmpty()) { // 영화 스케줄이 존재하는 경우
            throw new TheaterException(TheaterErrorCode.EXIST_SCHEDULE);
        }

        Theater deletedTheater = theater.toBuilder()
            .seatList(new ArrayList<>()) // 연관관계 해제시킴으로서 좌석 삭제(orphanRemoval)
            .deleteDate(LocalDateTime.now())
            .build();
        theaterRepository.save(deletedTheater);
    }

}
