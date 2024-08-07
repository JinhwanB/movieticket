package com.jh.movieticket.theater.service;

import com.jh.movieticket.config.CacheName;
import com.jh.movieticket.movie.repository.MovieScheduleRepository;
import com.jh.movieticket.theater.domain.Seat;
import com.jh.movieticket.theater.domain.Theater;
import com.jh.movieticket.theater.dto.TheaterCreateDto;
import com.jh.movieticket.theater.dto.TheaterModifyDto;
import com.jh.movieticket.theater.dto.TheaterServiceDto;
import com.jh.movieticket.theater.exception.TheaterErrorCode;
import com.jh.movieticket.theater.exception.TheaterException;
import com.jh.movieticket.theater.repository.TheaterRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
    private final CacheManager redisCacheManager;

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

        // 좌석 함께 저장
        IntStream.range(1, seatCnt + 1)
            .mapToObj(i -> Seat.builder()
                    .seatNo(i)
                    .theater(theater)
                    .build())
            .forEach(theater::addSeat);

        Theater savedTheater = theaterRepository.save(theater);

        return savedTheater.toServiceDto();
    }

    /**
     * 상영관 수정 서비스
     *
     * @param theaterModifyDto 수정할 정보
     * @return 수정된 상영관
     */
    @CacheEvict(key = "#theaterModifyDto.originName", value = CacheName.THEATER_CACHE_NAME)
    @CachePut(key = "#theaterModifyDto.changedName", value = CacheName.THEATER_CACHE_NAME)
    public TheaterServiceDto updateTheater(TheaterModifyDto.Request theaterModifyDto) {

        String originName = theaterModifyDto.getOriginName();
        String changedName = theaterModifyDto.getChangedName();

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
     * @param id 삭제할 상영관 pk
     */
    public void deleteTheater(Long id) {

        Theater theater = theaterRepository.findByIdAndDeleteDateIsNull(id)
            .orElseThrow(() -> new TheaterException(TheaterErrorCode.NOT_FOUND_THEATER));

        if (movieScheduleRepository.existsByTheater(theater)) { // 영화 스케줄이 존재하는 경우
            throw new TheaterException(TheaterErrorCode.EXIST_SCHEDULE);
        }

        Cache cache = redisCacheManager.getCache(CacheName.THEATER_CACHE_NAME);
        if(cache != null){
            cache.evict(theater.getName()); // 캐시 제거
        }

        Theater deletedTheater = theater.toBuilder()
            .seatList(new ArrayList<>()) // 연관관계 해제시킴으로서 좌석 삭제(orphanRemoval)
            .deleteDate(LocalDateTime.now())
            .build();
        theaterRepository.save(deletedTheater);
    }

    /**
     * 상영관 조회 서비스
     *
     * @param name 조회할 상영관 이름
     * @return 조회된 상영관
     */
    @Cacheable(key = "#name", value = CacheName.THEATER_CACHE_NAME)
    @Transactional(readOnly = true)
    public TheaterServiceDto verify(String name) {

        Theater theater = theaterRepository.findByNameAndDeleteDate(name, null)
            .orElseThrow(() -> new TheaterException(TheaterErrorCode.NOT_FOUND_THEATER));

        return theater.toServiceDto();
    }

    /**
     * 전체 상영관 조회 서비스
     *
     * @param pageable 페이징 설정
     * @return 페이징 처리된 전체 상영관 리스트
     */
    @Transactional(readOnly = true)
    public Page<TheaterServiceDto> verifyAll(Pageable pageable) {

        Page<Theater> theaterList = theaterRepository.findAllByDeleteDateIsNull(pageable);
        List<TheaterServiceDto> theaterServiceDtoList = theaterList.getContent().stream()
            .map(Theater::toServiceDto)
            .toList();

        return new PageImpl<>(theaterServiceDtoList, pageable, theaterServiceDtoList.size());
    }
}
