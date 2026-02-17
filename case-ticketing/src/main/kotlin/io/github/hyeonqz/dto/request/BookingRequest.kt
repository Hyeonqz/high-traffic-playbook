package io.github.hyeonqz.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class BookingRequest(
    @field:Positive(message = "seatId는 양수여야 합니다.")
    val seatId: Long,

    @field:NotBlank(message = "userId는 필수입니다.")
    val userId: String,

    @field:NotBlank(message = "idempotencyKey는 필수입니다.")
    val idempotencyKey: String,
)
