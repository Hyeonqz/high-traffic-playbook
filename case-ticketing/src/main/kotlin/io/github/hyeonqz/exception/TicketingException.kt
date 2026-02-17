package io.github.hyeonqz.exception

import org.springframework.http.HttpStatus

class TicketingException(
    val errorCode: ErrorCode,
    message: String = errorCode.message,
) : RuntimeException(message)

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String,
) {
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "SEAT_NOT_FOUND", "좌석을 찾을 수 없습니다."),
    SEAT_ALREADY_BOOKED(HttpStatus.CONFLICT, "SEAT_ALREADY_BOOKED", "이미 예약된 좌석입니다."),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKING_NOT_FOUND", "예약을 찾을 수 없습니다."),
    BOOKING_FORBIDDEN(HttpStatus.FORBIDDEN, "BOOKING_FORBIDDEN", "다른 사용자의 예약을 취소할 수 없습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_NOT_FOUND", "스케줄을 찾을 수 없습니다."),
}
