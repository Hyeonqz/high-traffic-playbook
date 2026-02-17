package io.github.hyeonqz.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "booking",
    uniqueConstraints = [UniqueConstraint(name = "uk_booking_idempotency", columnNames = ["user_id", "idempotency_key"])]
)
class Booking(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    val seat: Seat,

    @Column(name = "user_id", nullable = false, length = 100)
    val userId: String,

    // 클라이언트가 생성한 UUID — 네트워크 재시도 시 동일 결과 반환 보장
    @Column(name = "idempotency_key", nullable = false, length = 100)
    val idempotencyKey: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: BookingStatus = BookingStatus.CONFIRMED,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun cancel() {
        check(status == BookingStatus.CONFIRMED) { "이미 취소된 예약입니다." }
        status = BookingStatus.CANCELLED
    }
}

enum class BookingStatus {
    CONFIRMED, CANCELLED
}
