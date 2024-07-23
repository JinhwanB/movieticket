package com.jh.movieticket.reservation.service;

import com.jh.movieticket.config.CacheName;
import com.jh.movieticket.member.domain.Member;
import com.jh.movieticket.member.exception.MemberErrorCode;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.member.repository.MemberRepository;
import com.jh.movieticket.movie.domain.MovieSchedule;
import com.jh.movieticket.movie.domain.MovieScheduleSeat;
import com.jh.movieticket.movie.domain.SeatType;
import com.jh.movieticket.movie.exception.MovieScheduleErrorCode;
import com.jh.movieticket.movie.exception.MovieScheduleException;
import com.jh.movieticket.movie.exception.MovieScheduleSeatErrorCode;
import com.jh.movieticket.movie.exception.MovieScheduleSeatException;
import com.jh.movieticket.movie.repository.MovieScheduleRepository;
import com.jh.movieticket.movie.repository.MovieScheduleSeatRepository;
import com.jh.movieticket.reservation.domain.Reservation;
import com.jh.movieticket.reservation.dto.ReservationCreateDto;
import com.jh.movieticket.reservation.dto.ReservationServiceDto;
import com.jh.movieticket.reservation.exception.ReservationErrorCode;
import com.jh.movieticket.reservation.exception.ReservationException;
import com.jh.movieticket.reservation.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final MovieScheduleRepository movieScheduleRepository;
    private final MovieScheduleSeatRepository movieScheduleSeatRepository;
    private final RedisTemplate<String, ReservationServiceDto> reservationRedisTemplate;

    private static final int TOTAL_SIZE = 4;
    private static int num = 0;

    /**
     * 예약 생성
     *
     * @param createRequest 생성할 예약 정보 dto
     * @return 생성된 예약 dto
     */
    public ReservationServiceDto createReservation(ReservationCreateDto.Request createRequest) {

        String reservationNumber = getReservationNumber(LocalDate.now());
        // 예약 번호가 중복되는 경우 중복되지 않을 때까지 반복
        while (reservationRepository.existsByReservationNumber(reservationNumber)) {
            reservationNumber = getReservationNumber(LocalDate.now());
        }

        Member member = memberRepository.findByUserIdAndDeleteDate(createRequest.getMemberId(),
                null)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        MovieSchedule movieSchedule = movieScheduleRepository.findById(
                createRequest.getMovieScheduleId())
            .orElseThrow(
                () -> new MovieScheduleException(MovieScheduleErrorCode.NOT_FOUND_MOVIE_SCHEDULE));

        MovieScheduleSeat movieScheduleSeat = movieSchedule.getMovieScheduleSeatList().stream()
            .filter(mss -> mss.getSeat().getSeatNo() == createRequest.getSeatNo())
            .findFirst()
            .orElseThrow(
                () -> new MovieScheduleSeatException(
                    MovieScheduleSeatErrorCode.NOT_FOUND_MOVIE_SCHEDULE_SEAT))
            .toBuilder()
            .status(SeatType.BOOKED)
            .build();
        movieScheduleSeatRepository.save(movieScheduleSeat); // 좌석 예약 상태로 변경

        Reservation reservation = Reservation.builder()
            .reservationNumber(reservationNumber)
            .movieSchedule(movieSchedule)
            .seatNo(createRequest.getSeatNo())
            .member(member)
            .build();

        ReservationServiceDto serviceDto = reservationRepository.save(reservation).toServiceDto();

        String reservationKey = CacheName.RESERVATION_CACHE_NAME + "::" + serviceDto.getId();
        reservationRedisTemplate.opsForValue()
            .set(reservationKey, serviceDto); // redis에 저장

        return serviceDto;
    }

    /**
     * 예약 삭제
     *
     * @param reservationId 예약 pk
     */
    @CacheEvict(value = CacheName.RESERVATION_CACHE_NAME, key = "#reservationId")
    public void deleteReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(
                () -> new ReservationException(ReservationErrorCode.NOT_FOUND_RESERVATION));

        int seatNo = reservation.getSeatNo();
        MovieScheduleSeat movieScheduleSeat = reservation.getMovieSchedule()
            .getMovieScheduleSeatList().stream()
            .filter(mss -> mss.getSeat().getSeatNo() == seatNo)
            .findFirst()
            .orElseThrow(() -> new MovieScheduleSeatException(
                MovieScheduleSeatErrorCode.NOT_FOUND_MOVIE_SCHEDULE_SEAT))
            .toBuilder()
            .status(SeatType.AVAILABLE)
            .build();
        movieScheduleSeatRepository.save(movieScheduleSeat); // 좌석 예약 가능한 상태로 변경

        reservation = reservation.toBuilder()
            .deleteDate(LocalDateTime.now())
            .build();
        reservationRepository.save(reservation);
    }

    /**
     * 예약 조회
     *
     * @param reservationId 예약 pk
     * @return 조회된 예약 dto
     */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.RESERVATION_CACHE_NAME, key = "#reservationId")
    public ReservationServiceDto verifyReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(
                () -> new ReservationException(ReservationErrorCode.NOT_FOUND_RESERVATION));

        return reservation.toServiceDto();
    }

    /**
     * 회원과 관련된 예약 리스트 페이징하여 조회
     *
     * @param memberId 회원 아이디
     * @param pageable 페이징 정보
     * @return 페이징 처리된 예약 리스트
     */
    @Transactional(readOnly = true)
    public Page<ReservationServiceDto> verifyAllByMemberReservation(String memberId,
        Pageable pageable) {

        Page<Reservation> reservationPage = reservationRepository.findAllByMemberId(memberId,
            pageable);
        List<ReservationServiceDto> reservationServiceDtoList = reservationPage.getContent()
            .stream()
            .map(Reservation::toServiceDto)
            .toList();

        return new PageImpl<>(reservationServiceDtoList, pageable,
            reservationServiceDtoList.size());
    }

    /**
     * 예약 전체 리스트 페이징하여 조회
     *
     * @param pageable 페이징 정보
     * @return 페이징 처리된 예약 전체 리스트
     */
    @Transactional(readOnly = true)
    public Page<ReservationServiceDto> verifyAllReservation(Pageable pageable) {

        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);
        List<ReservationServiceDto> reservationServiceDtoList = reservationPage.getContent()
            .stream()
            .map(Reservation::toServiceDto)
            .toList();

        return new PageImpl<>(reservationServiceDtoList, pageable,
            reservationServiceDtoList.size());
    }

    /**
     * 예약 번호 생성 메소드
     *
     * @param now 오늘 날짜
     * @return 생성된 예약 번호
     */
    private String getReservationNumber(LocalDate now) {

        StringBuilder sb = new StringBuilder();
        String monthValue = now.getMonthValue() < 10 ? String.valueOf(now.getMonthValue())
            : "0" + now.getMonthValue();
        String dayOfMonth = now.getDayOfMonth() < 10 ? String.valueOf(now.getDayOfMonth())
            : "0" + now.getDayOfMonth();

        sb.append(monthValue).append(dayOfMonth); // 오늘 날짜(0910, 1021, 1001) 4자리

        int size = TOTAL_SIZE - String.valueOf(num).length();
        IntStream.range(0, size)
            .forEach(i -> sb.append(0));

        sb.append(num); // 4자리 숫자 (0000 ~ 9999)
        num++;
        if (num > 9999) {
            num = 0;
        }

        return sb.toString();
    }
}
