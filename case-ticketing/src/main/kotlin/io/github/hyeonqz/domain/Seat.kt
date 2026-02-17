package io.github.hyeonqz.domain

import jakarta.persistence.*

@Entity
@Table(
    name = "seat",
    uniqueConstraints = [UniqueConstraint(name = "uk_seat_position", columnNames = ["schedule_id", "car_number", "seat_number"])]
)
class Seat(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    val schedule: Schedule,

    @Column(name = "car_number", nullable = false)
    val carNumber: Int,

    @Column(name = "seat_number", nullable = false, length = 10)
    val seatNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_class", nullable = false, length = 20)
    val seatClass: SeatClass,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: SeatStatus = SeatStatus.AVAILABLE,

    // 낙관적 락: UPDATE seat SET version = version+1 WHERE id=? AND version=?
    // 동시 요청 중 단 1건만 version 일치 → 성공, 나머지 → ObjectOptimisticLockingFailureException
    @Version
    var version: Long = 0,
) {
    fun book() {
        check(status == SeatStatus.AVAILABLE) { "이미 예약된 좌석입니다." }
        status = SeatStatus.BOOKED
    }

    fun cancel() {
        check(status == SeatStatus.BOOKED) { "예약된 상태가 아닙니다." }
        status = SeatStatus.AVAILABLE
    }
}

enum class SeatStatus {
    AVAILABLE, BOOKED
}

enum class SeatClass {
    FIRST, STANDARD
}
