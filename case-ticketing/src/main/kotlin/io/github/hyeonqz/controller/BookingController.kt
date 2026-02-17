package io.github.hyeonqz.controller

import io.github.hyeonqz.dto.request.BookingRequest
import io.github.hyeonqz.dto.response.BookingResponse
import io.github.hyeonqz.service.BookingService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/apis/v1/bookings")
class BookingController(
    private val bookingService: BookingService,
) {

    @PostMapping
    fun reserve(@RequestBody @Valid request: BookingRequest): ResponseEntity<BookingResponse> {
        val response = bookingService.reserve(request)
        // 동일 idempotencyKey 재요청 시에도 201 대신 200 반환 구분은 서비스 레이어에서 처리
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{bookingId}")
    fun getBooking(@PathVariable bookingId: Long): ResponseEntity<BookingResponse> {
        return ResponseEntity.ok(bookingService.getBooking(bookingId))
    }

    @DeleteMapping("/{bookingId}")
    fun cancel(
        @PathVariable bookingId: Long,
        @RequestParam userId: String,
    ): ResponseEntity<Void> {
        bookingService.cancel(bookingId, userId)
        return ResponseEntity.noContent().build()
    }
}
