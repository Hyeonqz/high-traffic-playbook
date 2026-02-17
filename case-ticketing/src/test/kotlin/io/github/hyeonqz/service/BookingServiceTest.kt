package io.github.hyeonqz.service

import io.github.hyeonqz.domain.*
import io.github.hyeonqz.dto.request.BookingRequest
import io.github.hyeonqz.exception.ErrorCode
import io.github.hyeonqz.exception.TicketingException
import io.github.hyeonqz.repository.BookingRepository
import io.github.hyeonqz.repository.SeatRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class BookingServiceTest {

    private val seatRepository: SeatRepository = mockk()
    private val bookingRepository: BookingRepository = mockk()
    private val bookingService = BookingService(seatRepository, bookingRepository)

    private lateinit var train: Train
    private lateinit var schedule: Schedule
    private lateinit var seat: Seat

    @BeforeEach
    fun setUp() {
        train = Train(id = 1L, trainNumber = "KTX101", name = "KTX 101", trainType = TrainType.KTX)
        schedule = Schedule(
            id = 1L,
            train = train,
            departureDate = LocalDate.of(2025, 1, 25),
            departureTime = LocalTime.of(8, 0),
            arrivalTime = LocalTime.of(10, 30),
            departureStation = "서울",
            arrivalStation = "부산",
        )
        seat = Seat(id = 1L, schedule = schedule, carNumber = 1, seatNumber = "1A", seatClass = SeatClass.FIRST)
    }

    @Test
    @DisplayName("정상 예약 성공")
    fun `reserve - 정상 예약 성공`() {
        // given
        val request = BookingRequest(seatId = 1L, userId = "user-1", idempotencyKey = "key-1")
        val expectedBooking = Booking(id = 1L, seat = seat, userId = "user-1", idempotencyKey = "key-1")

        every { bookingRepository.findByUserIdAndIdempotencyKey("user-1", "key-1") } returns null
        every { seatRepository.findById(1L) } returns Optional.of(seat)
        every { bookingRepository.save(any()) } returns expectedBooking

        // when
        val response = bookingService.reserve(request)

        // then
        assertThat(response.userId).isEqualTo("user-1")
        assertThat(response.status).isEqualTo(BookingStatus.CONFIRMED)
        assertThat(seat.status).isEqualTo(SeatStatus.BOOKED)
    }

    @Test
    @DisplayName("이미 BOOKED 상태인 좌석 예약 시도 → SEAT_ALREADY_BOOKED 예외")
    fun `reserve - 이미 예약된 좌석이면 예외 발생`() {
        // given
        seat.book()
        val request = BookingRequest(seatId = 1L, userId = "user-2", idempotencyKey = "key-2")

        every { bookingRepository.findByUserIdAndIdempotencyKey("user-2", "key-2") } returns null
        every { seatRepository.findById(1L) } returns Optional.of(seat)

        // when & then
        val thrown = org.junit.jupiter.api.Assertions.assertThrows(TicketingException::class.java) {
            bookingService.reserve(request)
        }
        assertThat(thrown.errorCode).isEqualTo(ErrorCode.SEAT_ALREADY_BOOKED)
    }

    @Test
    @DisplayName("동일 idempotencyKey 재요청 → DB 조회 없이 기존 Booking 반환")
    fun `reserve - 동일 idempotencyKey 재요청은 기존 결과 반환`() {
        // given
        val existingBooking = Booking(id = 1L, seat = seat, userId = "user-1", idempotencyKey = "key-1")
        val request = BookingRequest(seatId = 1L, userId = "user-1", idempotencyKey = "key-1")

        every { bookingRepository.findByUserIdAndIdempotencyKey("user-1", "key-1") } returns existingBooking

        // when
        val response = bookingService.reserve(request)

        // then
        assertThat(response.bookingId).isEqualTo(1L)
        // seatRepository는 호출되지 않아야 함
        verify(exactly = 0) { seatRepository.findById(any()) }
    }

    @Test
    @DisplayName("다른 사람의 예약 취소 시도 → BOOKING_FORBIDDEN 예외")
    fun `cancel - 다른 사용자의 예약 취소는 예외 발생`() {
        // given
        val booking = Booking(id = 1L, seat = seat, userId = "user-1", idempotencyKey = "key-1")
        every { bookingRepository.findById(1L) } returns Optional.of(booking)

        // when & then
        val thrown = org.junit.jupiter.api.Assertions.assertThrows(TicketingException::class.java) {
            bookingService.cancel(1L, "user-999")
        }
        assertThat(thrown.errorCode).isEqualTo(ErrorCode.BOOKING_FORBIDDEN)
    }

    @Test
    @DisplayName("예약 취소 성공 → booking CANCELLED, seat AVAILABLE")
    fun `cancel - 정상 취소 성공`() {
        // given
        seat.book()
        val booking = Booking(id = 1L, seat = seat, userId = "user-1", idempotencyKey = "key-1")
        every { bookingRepository.findById(1L) } returns Optional.of(booking)

        // when
        bookingService.cancel(1L, "user-1")

        // then
        assertThat(booking.status).isEqualTo(BookingStatus.CANCELLED)
        assertThat(seat.status).isEqualTo(SeatStatus.AVAILABLE)
    }
}
