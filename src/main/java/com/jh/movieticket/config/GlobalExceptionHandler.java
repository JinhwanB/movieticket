package com.jh.movieticket.config;

import com.jh.movieticket.auth.TokenException;
import com.jh.movieticket.mail.exception.MailException;
import com.jh.movieticket.member.exception.MemberException;
import com.jh.movieticket.theater.exception.TheaterException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 404 에러 핸들러
    @ExceptionHandler(NoHandlerFoundException.class)
    private ResponseEntity<GlobalApiResponse<?>> handleNotFoundException(
        NoHandlerFoundException e) {

        log.error("404 NotFound = {}", e.getMessage());

        return new ResponseEntity<>(
            GlobalApiResponse.toGlobalResponseFail(HttpStatus.NOT_FOUND, "요청한 페이지를 찾을 수 없습니다."),
            HttpStatus.NOT_FOUND);
    }

    // 405 에러 핸들러
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<GlobalApiResponse<?>> handleNotSupportedException(
        HttpRequestMethodNotSupportedException e) {

        log.error("405 NotSupported = {}", e.getMessage());

        return new ResponseEntity<>(
            GlobalApiResponse.toGlobalResponseFail(HttpStatus.METHOD_NOT_ALLOWED,
                "해당 url을 지원하지 않습니다. HTTP Method(GET, PUT, POST, DELETE)가 정확한지 확인해주세요."),
            HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 유효성 검증 에러 핸들러(requestBody) -> 400 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<List<GlobalApiResponse<?>>> handleValidException(
        MethodArgumentNotValidException e) {

        log.error("request 유효성 검사 실패");

        List<GlobalApiResponse<?>> list = new ArrayList<>();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            GlobalApiResponse<?> response = GlobalApiResponse.toGlobalResponseFail(
                HttpStatus.BAD_REQUEST,
                fieldError.getDefaultMessage());
            list.add(response);
        }

        return ResponseEntity.badRequest().body(list);
    }

    // 유효성 검증 에러 핸들러(pathVariable, requestParam) -> 400 에러
    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<List<GlobalApiResponse<?>>> handleValidException2(
        ConstraintViolationException e) {

        log.error("pathVariable 또는 requestParam 유효성 검사 실패");

        List<GlobalApiResponse<?>> list = new ArrayList<>();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            GlobalApiResponse<Object> response = GlobalApiResponse.toGlobalResponseFail(
                HttpStatus.BAD_REQUEST,
                constraintViolation.getMessage());
            list.add(response);
        }

        return ResponseEntity.badRequest().body(list);
    }

    // 회원 관련 에러 핸들러 -> 400 에러
    @ExceptionHandler(MemberException.class)
    private ResponseEntity<GlobalApiResponse<?>> handleMemberException(MemberException e) {

        log.error("회원 관련 exception = {}", e.getMemberErrorCode().getMessage());

        return ResponseEntity.badRequest()
            .body(GlobalApiResponse.toGlobalResponseFail(HttpStatus.BAD_REQUEST,
                e.getMemberErrorCode().getMessage()));
    }

    // 메일 관련 에러 핸들러 -> 400 에러
    @ExceptionHandler(MailException.class)
    private ResponseEntity<GlobalApiResponse<?>> handleMailException(MailException e) {

        log.error("메일 관련 exception = {}", e.getMailErrorCode().getMessage());

        return ResponseEntity.badRequest()
            .body(GlobalApiResponse.toGlobalResponseFail(HttpStatus.BAD_REQUEST,
                e.getMailErrorCode().getMessage()));
    }

    // 토큰 관련 에러 핸들러 -> 400 에러
    @ExceptionHandler(TokenException.class)
    private ResponseEntity<GlobalApiResponse<?>> handleTokenException(TokenException e) {

        log.error("토큰 관련 exception = {}", e.getTokenErrorCode().getMessage());

        return ResponseEntity.badRequest()
            .body(GlobalApiResponse.toGlobalResponseFail(HttpStatus.BAD_REQUEST,
                e.getTokenErrorCode().getMessage()));
    }

    // 상영관 관련 에러 핸들러 -> 400 에러
    @ExceptionHandler(TheaterException.class)
    private ResponseEntity<GlobalApiResponse<?>> handleTheaterException(TheaterException e) {

        log.error("상영관 관련 exception = {}", e.getTheaterErrorCode().getMessage());

        return ResponseEntity.badRequest()
            .body(GlobalApiResponse.toGlobalResponseFail(HttpStatus.BAD_REQUEST,
                e.getTheaterErrorCode().getMessage()));
    }
}