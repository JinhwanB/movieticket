package com.jh.movieticket.reservation.controller;

import com.jh.movieticket.config.GlobalApiResponse;
import com.jh.movieticket.reservation.dto.ReservationCreateDto;
import com.jh.movieticket.reservation.dto.ReservationServiceDto;
import com.jh.movieticket.reservation.dto.ReservationVerifyDto;
import com.jh.movieticket.reservation.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 생성 컨트롤러
     *
     * @param createRequest 예약 생성 정보 dto
     * @return 성공 시 201 코드와 생성된 예약 dto, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/reservation")
    public ResponseEntity<GlobalApiResponse<ReservationCreateDto.Response>> createReservationController(
        @Valid @RequestBody ReservationCreateDto.Request createRequest) {

        ReservationServiceDto reservationServiceDto = reservationService.createReservation(
            createRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(GlobalApiResponse.toGlobalResponse(HttpStatus.CREATED,
                reservationServiceDto.toCreateResponse()));
    }

    /**
     * 예약 삭제 컨트롤러
     *
     * @param reservationId 예약 pk
     * @return 성공 시 200 코드, 실패 시 에러코드와 에러메시지
     */
    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<GlobalApiResponse<?>> deleteReservationController(
        @NotNull(message = "pk값은 null일 수 없습니다.") @Positive(message = "pk값은 0 또는 음수일 수 없습니다.") @PathVariable Long reservationId) {

        reservationService.deleteReservation(reservationId);

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(HttpStatus.OK, null));
    }

    /**
     * 예약 조회 컨트롤러
     *
     * @param reservationId 예약 pk
     * @return 성공 시 200 코드와 조회된 예약 dto, 실패 시 에러코드와 에러메시지
     */
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<GlobalApiResponse<ReservationVerifyDto.Response>> verifyReservationController(
        @NotNull(message = "pk값은 null일 수 없습니다.") @Positive(message = "pk값은 0 또는 음수일 수 없습니다.") @PathVariable Long reservationId) {

        ReservationServiceDto reservationServiceDto = reservationService.verifyReservation(
            reservationId);

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(HttpStatus.OK,
            reservationServiceDto.toVerifyResponse()));
    }
}
