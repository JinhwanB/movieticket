package com.jh.movieticket.movie.controller;

import com.jh.movieticket.config.GlobalApiResponse;
import com.jh.movieticket.movie.dto.MovieCreateDto;
import com.jh.movieticket.movie.dto.MovieModifyDto;
import com.jh.movieticket.movie.dto.MovieServiceDto;
import com.jh.movieticket.movie.dto.MovieVerifyDto;
import com.jh.movieticket.movie.service.MovieService;
import com.jh.movieticket.validation.IsImage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Validated
public class MovieController {

    private final MovieService movieService;

    /**
     * 영화 생성 컨트롤러
     *
     * @param multipartFile 포스터
     * @param createRequest 영화 생성 dto
     * @return 성공 시 201 코드와 생성된 영화 dto, 실패 시 에러코드와 에러메시지
     */
    @PostMapping("/movie")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalApiResponse<MovieCreateDto.Response>> movieCreateController(
        @IsImage @RequestPart
        MultipartFile multipartFile, @Valid @RequestPart MovieCreateDto.Request createRequest) {

        MovieServiceDto movieServiceDto = movieService.createMovie(multipartFile, createRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            GlobalApiResponse.toGlobalResponse(HttpStatus.CREATED,
                movieServiceDto.toCreateResponse()));
    }

    /**
     * 영화 수정 컨트롤러
     *
     * @param multipartFile 수정할 포스터
     * @param modifyRequest 수정할 영화 정보 dto
     * @return 성공 시 200 코드와 수정된 영화 dto, 실패 시 에러코드와 에러메시지
     */
    @PutMapping("/movie")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalApiResponse<MovieModifyDto.Response>> movieUpdateController(
        @IsImage @RequestPart MultipartFile multipartFile,
        @Valid @RequestPart MovieModifyDto.Request modifyRequest) {

        MovieServiceDto serviceDto = movieService.updateMovie(multipartFile, modifyRequest);

        return ResponseEntity.ok(
            GlobalApiResponse.toGlobalResponse(HttpStatus.OK, serviceDto.toModifyResponse()));
    }

    /**
     * 영화 삭제 컨트롤러
     *
     * @param id 삭제할 영화 pk
     * @return 성공 시 200 코드, 실패시 에러코드와 에러메시지
     */
    @DeleteMapping("/movie/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalApiResponse<?>> movieDeleteController(@PathVariable Long id) {

        movieService.deleteMovie(id);

        return ResponseEntity.ok(GlobalApiResponse.toGlobalResponseFail(HttpStatus.OK, null));
    }

    /**
     * 영화 조회 컨트롤러
     *
     * @param movieTitle 조회할 영화 제목
     * @return 성공 시 200 코드와 조회된 영화 dto, 실패 시 에러코드와 에러메시지
     */
    @GetMapping("/movie/{movieTitle}")
    public ResponseEntity<GlobalApiResponse<MovieVerifyDto.Response>> movieVerifyController(
        @PathVariable String movieTitle) {

        MovieServiceDto serviceDto = movieService.verifyMovie(movieTitle);

        return ResponseEntity.ok(
            GlobalApiResponse.toGlobalResponse(HttpStatus.OK, serviceDto.toVerifyResponse()));
    }
}
