package io.github.hyeonqz.repository

import io.github.hyeonqz.domain.Seat
import io.github.hyeonqz.domain.SeatStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SeatRepository : JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s WHERE s.schedule.id = :scheduleId")
    fun findByScheduleId(@Param("scheduleId") scheduleId: Long): List<Seat>

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.schedule.id = :scheduleId AND s.status = :status")
    fun countByScheduleIdAndStatus(
        @Param("scheduleId") scheduleId: Long,
        @Param("status") status: SeatStatus,
    ): Long
}
