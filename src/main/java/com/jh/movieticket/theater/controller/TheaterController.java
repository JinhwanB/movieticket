package com.jh.movieticket.theater.controller;

import com.jh.movieticket.config.GlobalApiResponse;
import com.jh.movieticket.theater.dto.TheaterCreateDto;
import com.jh.movieticket.theater.dto.TheaterModifyDto;
import com.jh.movieticket.theater.dto.TheaterServiceDto;
import com.jh.movieticket.theater.dto.TheaterVerifyDto;
import com.jh.movieticket.theater.dto.TheaterVerifyDto.Response;
import com.jh.movieticket.theater.service.TheaterService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/theaters")
@RequiredArgsConstructor
@Validated
public class TheaterController {

    private final TheaterService theaterService;

    /**
     * 상영관 생성 컨트롤러
     *
     * @param request 상영관 생성 정보
     * @return 성공 시 201 코드와 생성된 상영관, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/theater")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalApiResponse<TheaterCreateDto.Response>> createOfTheater(
        @Valid @RequestBody TheaterCreateDto.Request request) {

        TheaterServiceDto theater = theaterService.createTheater(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalApiResponse.toGlobalResponse(HttpStatus.CREATED, theater.toCreateResponse()));
    }

    /**
     * 상영관 수정 컨트롤러
     *
     * @param request 수정할 정보
     * @return 성공 시 200 코드와 수정된 상영관, 실패 시 에러코드와 에러메시지
     */
    @PutMapping("/theater")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalApiResponse<TheaterModifyDto.Response>> updateOfTheater(
        @Valid @RequestBody TheaterModifyDto.Request request) {

        TheaterServiceDto theater = theaterService.updateTheater(request);

        return ResponseEntity.ok(
            GlobalApiResponse.toGlobalResponse(HttpStatus.OK, theater.toModifyResponse()));
    }

    /**
     * 상영관 삭제 컨트롤러
     *
     * @param id 삭제할 상영관 pk
     * @return 성공 시 200 코드, 실패 시 에러코드와 에러메시지
     */
    @DeleteMapping("/theater/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalApiResponse<?>> deleteOfTheater(@NotNull(message = "pk는 null일 수 없습니다.") @Positive(message = "pk값은 0 또는 음수일 수 없습니다.") @PathVariable Long id) {

        theaterService.deleteTheater(id);

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(HttpStatus.OK, null));
    }

    /**
     * 상영관 조회 컨트롤러
     *
     * @param theaterName 조회할 상영관 이름
     * @return 성공 시 200 코드와 조회된 상영관, 실패 시 에러코드와 에러메시지
     */
    @GetMapping("/theater/{theaterName}/detail")
    public ResponseEntity<GlobalApiResponse<TheaterVerifyDto.Response>> verifyOfTheater(
        @NotBlank(message = "삭제할 상영관 이름을 입력해주세요.") @Pattern(regexp = "^[1-9][0-9]?관$", message = "올바른 상영관 이름으로 입력해주세요. (ex. 1관, 2관, 10관)") @PathVariable String theaterName) {

        TheaterServiceDto theater = theaterService.verify(theaterName);

        return ResponseEntity.ok(
            GlobalApiResponse.toGlobalResponse(HttpStatus.OK, theater.toVerifyResponse()));
    }

    /**
     * 전체 상영관 리스트 조회 컨트롤러
     *
     * @param pageable 페이징 처리 설정
     * @return 페이징 처리된 전체 상영관 리스트
     */
    @GetMapping
    public ResponseEntity<GlobalApiResponse<Page<TheaterVerifyDto.Response>>> verifyAllOfTheater(
        @PageableDefault(sort = "name", direction = Direction.ASC)
        Pageable pageable) {

        Page<TheaterServiceDto> theaterServiceDtos = theaterService.verifyAll(pageable);
        List<Response> responseList = theaterServiceDtos.getContent().stream()
            .map(TheaterServiceDto::toVerifyResponse)
            .toList();

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponse(HttpStatus.OK,
            new PageImpl<>(responseList, pageable, responseList.size())));
    }
}
