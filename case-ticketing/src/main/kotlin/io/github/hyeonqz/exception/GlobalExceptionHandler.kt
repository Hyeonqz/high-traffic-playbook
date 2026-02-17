package io.github.hyeonqz.exception

import org.springframework.http.ResponseEntity
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler : BaseGlobalExceptionHandler() {

    @ExceptionHandler(TicketingException::class)
    fun handleTicketingException(e: TicketingException): ResponseEntity<ErrorResponse> =
        errorResponse(e.errorCode, e.message)

    // 낙관적 락 충돌: 동시 요청 중 version 불일치로 UPDATE 실패 → 409
    @ExceptionHandler(ObjectOptimisticLockingFailureException::class)
    fun handleOptimisticLockingFailure(e: ObjectOptimisticLockingFailureException): ResponseEntity<ErrorResponse> =
        errorResponse(TicketingErrorCode.SEAT_ALREADY_BOOKED)
}
