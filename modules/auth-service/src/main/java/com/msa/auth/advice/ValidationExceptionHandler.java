package com.msa.auth.advice;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ValidationExceptionHandler {
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<String>> handleWebExchangeBindException(WebExchangeBindException e) {
        String message = e.getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("잘못된 요청입니다.");
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<String>> handleConstraintViolationException(ConstraintViolationException e) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body("유효성 검증 실패: " + e.getMessage()));
    }
}
