package io.github.hyeonqz.service

import io.github.hyeonqz.domain.SeatStatus
import io.github.hyeonqz.dto.response.ScheduleResponse
import io.github.hyeonqz.dto.response.SeatResponse
import io.github.hyeonqz.exception.TicketingErrorCode
import io.github.hyeonqz.exception.TicketingException
import io.github.hyeonqz.repository.ScheduleRepository
import io.github.hyeonqz.repository.SeatRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ScheduleService(
    private val scheduleRepository: ScheduleRepository,
    private val seatRepository: SeatRepository,
) {

    @Transactional(readOnly = true)
    fun searchSchedules(date: LocalDate, from: String, to: String): List<ScheduleResponse> {
        return scheduleRepository.findByDateAndStations(date, from, to).map { schedule ->
            val availableCount = seatRepository.countByScheduleIdAndStatus(schedule.id, SeatStatus.AVAILABLE)
            ScheduleResponse.of(schedule, availableCount)
        }
    }

    @Transactional(readOnly = true)
    fun getSeats(scheduleId: Long): List<SeatResponse> {
        scheduleRepository.findById(scheduleId).orElseThrow {
            TicketingException(TicketingErrorCode.SCHEDULE_NOT_FOUND)
        }
        return seatRepository.findByScheduleId(scheduleId).map(SeatResponse::from)
    }
}
