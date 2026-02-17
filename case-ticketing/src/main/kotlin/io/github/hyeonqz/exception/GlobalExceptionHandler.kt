package io.github.hyeonqz.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(TicketingException::class)
    fun handleTicketingException(e: TicketingException): ResponseEntity<ErrorResponse> {
        val errorCode = e.errorCode
        return ResponseEntity
            .status(errorCode.status)
            .body(ErrorResponse(errorCode.code, e.message ?: errorCode.message))
    }

    // 낙관적 락 충돌: 동시 요청 중 version 불일치로 UPDATE 실패 → 409
    @ExceptionHandler(ObjectOptimisticLockingFailureException::class)
    fun handleOptimisticLockingFailure(e: ObjectOptimisticLockingFailureException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(ErrorCode.SEAT_ALREADY_BOOKED.code, ErrorCode.SEAT_ALREADY_BOOKED.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다."))
    }
}

data class ErrorResponse(
    val code: String,
    val message: String,
)
