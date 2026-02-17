package io.github.hyeonqz.repository

import io.github.hyeonqz.domain.Schedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ScheduleRepository : JpaRepository<Schedule, Long> {

    @Query("""
        SELECT s FROM Schedule s
        JOIN FETCH s.train
        WHERE s.departureDate = :date
          AND s.departureStation = :from
          AND s.arrivalStation = :to
    """)
    fun findByDateAndStations(
        @Param("date") date: LocalDate,
        @Param("from") from: String,
        @Param("to") to: String,
    ): List<Schedule>
}
