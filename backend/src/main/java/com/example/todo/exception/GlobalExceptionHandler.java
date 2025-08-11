package com.example.todo.exception;

import com.example.todo.common.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 유효성 검사 실패 처리 (DTO @Valid 검증 실패)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()   // 검증 결과 가져오기
                .getFieldErrors()   // 필드별 검증 실패목록
                .stream()   // 스트림 형태로 변환
                .map(e -> e.getField() + ": " + e.getDefaultMessage())  // 스트림 요소를 하나씩 변환(에러메시지)
                .collect(Collectors.joining(", "));  // 스트림의 모든 문자열을 하나로 합침(,로 구분)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseDTO.error(HttpStatus.BAD_REQUEST.value(), errors));
    }

    // 커스텀 DuplicateEmailException 처리 (409)
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleDuplicateEmailException(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponseDTO.error(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    // IllegalArgumentException 처리 (예:  로그인 정보 불일치)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseDTO.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    // 인증 관련 IllegalStateException 처리 (401 Unauthorized)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDTO.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    // 커스텀 TodoNotFoundException 처리
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleTodoNotFoundException(TodoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseDTO.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    // 커스텀 LoginFailedException 처리 (401 Unauthorized)
    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleLoginFailedException(LoginFailedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDTO.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleException(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다."));
    }
}
