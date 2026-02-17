package io.github.hyeonqz.exception

import org.springframework.http.HttpStatus

/**
 * Ticketing 도메인 에러 코드.
 *
 * 코드 체계:
 *   TKT-001 ~ 099 : 클라이언트 오류 (4xx)
 *   TKT-100 ~ 199 : 인증/인가 오류 (401, 403)
 */
enum class TicketingErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : ErrorCode {

    // ── 4xx Client Error ──────────────────────────────────────────────
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND,      "TKT-001", "좌석을 찾을 수 없습니다."),
    SEAT_ALREADY_BOOKED(HttpStatus.CONFLICT,  "TKT-002", "이미 예약된 좌석입니다."),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND,   "TKT-003", "예약을 찾을 수 없습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND,  "TKT-004", "스케줄을 찾을 수 없습니다."),

    // ── 4xx Auth Error ────────────────────────────────────────────────
    BOOKING_FORBIDDEN(HttpStatus.FORBIDDEN,   "TKT-101", "다른 사용자의 예약을 취소할 수 없습니다."),
}
