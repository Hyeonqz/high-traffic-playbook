package io.github.hyeonqz.controller

import io.github.hyeonqz.dto.response.ScheduleResponse
import io.github.hyeonqz.dto.response.SeatResponse
import io.github.hyeonqz.service.ScheduleService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/schedules")
class ScheduleController(
    private val scheduleService: ScheduleService,
) {

    @GetMapping
    fun searchSchedules(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        @RequestParam from: String,
        @RequestParam to: String,
    ): ResponseEntity<List<ScheduleResponse>> {
        return ResponseEntity.ok(scheduleService.searchSchedules(date, from, to))
    }

    @GetMapping("/{scheduleId}/seats")
    fun getSeats(@PathVariable scheduleId: Long): ResponseEntity<List<SeatResponse>> {
        return ResponseEntity.ok(scheduleService.getSeats(scheduleId))
    }
}
