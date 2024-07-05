package com.jh.movieticket.theater.service;

import com.jh.movieticket.theater.domain.Seat;
import com.jh.movieticket.theater.domain.Theater;
import com.jh.movieticket.theater.dto.TheaterCreateDto;
import com.jh.movieticket.theater.dto.TheaterServiceDto;
import com.jh.movieticket.theater.exception.TheaterErrorCode;
import com.jh.movieticket.theater.exception.TheaterException;
import com.jh.movieticket.theater.repository.SeatRepository;
import com.jh.movieticket.theater.repository.TheaterRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final SeatRepository seatRepository;

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
            .seatCnt(seatCnt)
            .build();
        Theater savedTheater = theaterRepository.save(theater);

        // 좌석 함께 저장
        List<Seat> seatList = new ArrayList<>();
        IntStream.range(1, seatCnt + 1)
            .forEach(i -> {
                Seat seat = Seat.builder()
                    .seatNo(i)
                    .theater(savedTheater)
                    .build();
                seatList.add(seat);
            });
        seatRepository.saveAll(seatList);

        return savedTheater.toServiceDto();
    }

    /**
     * 상영관 이름 수정 서비스
     *
     * @param originName  수정할 상영관 이름
     * @param changedName 수정하고자 하는 상영관 이름
     * @return 수정된 상영관 정보
     */
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
}