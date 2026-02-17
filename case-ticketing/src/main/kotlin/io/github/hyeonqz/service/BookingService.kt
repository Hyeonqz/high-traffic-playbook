package io.github.hyeonqz.service

import io.github.hyeonqz.domain.Booking
import io.github.hyeonqz.domain.SeatStatus
import io.github.hyeonqz.dto.request.BookingRequest
import io.github.hyeonqz.dto.response.BookingResponse
import io.github.hyeonqz.exception.TicketingErrorCode
import io.github.hyeonqz.exception.TicketingException
import io.github.hyeonqz.repository.BookingRepository
import io.github.hyeonqz.repository.SeatRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookingService(
    private val seatRepository: SeatRepository,
    private val bookingRepository: BookingRepository,
) {

    /**
     * 좌석 예약
     *
     * 2중 방어 구조:
     *  1. Idempotency Key 검증 — 동일 요청 재시도 시 기존 결과 반환
     *  2. 좌석 상태 확인 — BOOKED면 즉시 409
     *  3. 낙관적 락(seat.version) — 동시 요청 중 1건만 성공, 나머지 ObjectOptimisticLockingFailureException
     *  4. 예약 레코드 생성
     */
    @Transactional
    fun reserve(request: BookingRequest): BookingResponse {
        // 1단계: Idempotency Key — 재요청이면 기존 결과 그대로 반환
        val existing = bookingRepository.findByUserIdAndIdempotencyKey(request.userId, request.idempotencyKey)
        if (existing != null) {
            return BookingResponse.from(existing)
        }

        // 2단계: 좌석 조회
        val seat = seatRepository.findById(request.seatId).orElseThrow {
            TicketingException(TicketingErrorCode.SEAT_NOT_FOUND)
        }

        // 3단계: 좌석 상태 확인 (BOOKED → 즉시 409)
        if (seat.status == SeatStatus.BOOKED) {
            throw TicketingException(TicketingErrorCode.SEAT_ALREADY_BOOKED)
        }

        // 4단계: 낙관적 락으로 상태 변경
        // seat.book() 호출 후 트랜잭션 커밋 시점에 version 검증
        // version 불일치 → ObjectOptimisticLockingFailureException → GlobalExceptionHandler에서 409 처리
        seat.book()

        // 5단계: 예약 레코드 생성
        val booking = bookingRepository.save(
            Booking(
                seat = seat,
                userId = request.userId,
                idempotencyKey = request.idempotencyKey,
            )
        )

        return BookingResponse.from(booking)
    }

    /**
     * 예약 취소
     *
     * booking.status → CANCELLED, seat.status → AVAILABLE 원복
     */
    @Transactional
    fun cancel(bookingId: Long, userId: String) {
        val booking = bookingRepository.findById(bookingId).orElseThrow {
            TicketingException(TicketingErrorCode.BOOKING_NOT_FOUND)
        }

        if (booking.userId != userId) {
            throw TicketingException(TicketingErrorCode.BOOKING_FORBIDDEN)
        }

        booking.cancel()
        booking.seat.cancel()
    }

    @Transactional(readOnly = true)
    fun getBooking(bookingId: Long): BookingResponse {
        val booking = bookingRepository.findById(bookingId).orElseThrow {
            TicketingException(TicketingErrorCode.BOOKING_NOT_FOUND)
        }
        return BookingResponse.from(booking)
    }
}
