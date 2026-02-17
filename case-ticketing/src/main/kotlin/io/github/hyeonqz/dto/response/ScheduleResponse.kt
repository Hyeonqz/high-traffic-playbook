package io.github.hyeonqz.dto.response

import io.github.hyeonqz.domain.Schedule
import io.github.hyeonqz.domain.TrainType
import java.time.LocalDate
import java.time.LocalTime

data class ScheduleResponse(
    val scheduleId: Long,
    val trainNumber: String,
    val trainType: TrainType,
    val departureDate: LocalDate,
    val departureTime: LocalTime,
    val arrivalTime: LocalTime,
    val departureStation: String,
    val arrivalStation: String,
    val availableSeatCount: Long,
) {
    companion object {
        fun of(schedule: Schedule, availableSeatCount: Long) = ScheduleResponse(
            scheduleId = schedule.id,
            trainNumber = schedule.train.trainNumber,
            trainType = schedule.train.trainType,
            departureDate = schedule.departureDate,
            departureTime = schedule.departureTime,
            arrivalTime = schedule.arrivalTime,
            departureStation = schedule.departureStation,
            arrivalStation = schedule.arrivalStation,
            availableSeatCount = availableSeatCount,
        )
    }
}
