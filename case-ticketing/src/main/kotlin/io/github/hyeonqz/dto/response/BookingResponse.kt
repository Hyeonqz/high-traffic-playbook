package io.github.hyeonqz.dto.response

import io.github.hyeonqz.domain.Booking
import io.github.hyeonqz.domain.BookingStatus
import java.time.LocalDateTime

data class BookingResponse(
    val bookingId: Long,
    val seatId: Long,
    val userId: String,
    val status: BookingStatus,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(booking: Booking) = BookingResponse(
            bookingId = booking.id,
            seatId = booking.seat.id,
            userId = booking.userId,
            status = booking.status,
            createdAt = booking.createdAt,
        )
    }
}
