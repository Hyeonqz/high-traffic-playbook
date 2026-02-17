package io.github.hyeonqz.dto.response

import io.github.hyeonqz.domain.Seat
import io.github.hyeonqz.domain.SeatClass
import io.github.hyeonqz.domain.SeatStatus

data class SeatResponse(
    val seatId: Long,
    val carNumber: Int,
    val seatNumber: String,
    val seatClass: SeatClass,
    val status: SeatStatus,
) {
    companion object {
        fun from(seat: Seat) = SeatResponse(
            seatId = seat.id,
            carNumber = seat.carNumber,
            seatNumber = seat.seatNumber,
            seatClass = seat.seatClass,
            status = seat.status,
        )
    }
}
