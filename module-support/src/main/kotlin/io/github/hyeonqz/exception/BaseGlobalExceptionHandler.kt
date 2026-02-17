package io.github.hyeonqz.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 모든 독립 모듈의 GlobalExceptionHandler 가 상속받는 공통 예외 핸들러.
 *
 * - 공통 예외(JVM 기본 예외)는 CommonErrorCode 로 일관 처리
 * - 도메인 고유 예외는 각 모듈 GlobalExceptionHandler 에서 처리
 * - errorResponse() 헬퍼를 protected 로 노출하여 하위 핸들러에서도 동일한 변환 로직 재사용
 */
@RestControllerAdvice
abstract class BaseGlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ErrorResponse> =
        errorResponse(CommonErrorCode.INVALID_ARGUMENT, e.message)

    @ExceptionHandler(NullPointerException::class)
    fun handleNullPointer(e: NullPointerException): ResponseEntity<ErrorResponse> =
        errorResponse(CommonErrorCode.NULL_POINTER)

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<ErrorResponse> =
        errorResponse(CommonErrorCode.RUNTIME_ERROR)

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> =
        errorResponse(CommonErrorCode.INTERNAL_ERROR)

    protected fun errorResponse(
        errorCode: ErrorCode,
        message: String? = null,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(errorCode.status)
            .body(ErrorResponse(errorCode.code, message ?: errorCode.message))
}
