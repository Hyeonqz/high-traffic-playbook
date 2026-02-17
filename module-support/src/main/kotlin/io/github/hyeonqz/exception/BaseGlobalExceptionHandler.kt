package io.github.hyeonqz.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 모든 독립 모듈의 GlobalExceptionHandler 가 상속받는 공통 예외 핸들러.
 *
 * 처리 범위:
 * - IllegalArgumentException → 400 Bad Request
 * - NullPointerException     → 500 Internal Server Error
 * - RuntimeException         → 500 Internal Server Error
 * - Exception                → 500 Internal Server Error
 *
 * 도메인 고유 예외는 각 모듈 GlobalExceptionHandler 에서 처리한다.
 */
@RestControllerAdvice
abstract class BaseGlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("INVALID_ARGUMENT", e.message ?: "잘못된 요청입니다."))
    }

    @ExceptionHandler(NullPointerException::class)
    fun handleNullPointer(e: NullPointerException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("NULL_POINTER", "서버 내부 오류가 발생했습니다."))
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("RUNTIME_ERROR", "서버 내부 오류가 발생했습니다."))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다."))
    }
}
