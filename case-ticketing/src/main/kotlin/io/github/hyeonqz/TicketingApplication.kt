package io.github.hyeonqz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Ticketing Application
 * 코레일 유통 기차 선착순 예매 - 동시성 제어 및 대기열 관리 설계
 */
@SpringBootApplication
class TicketingApplication

fun main(args: Array<String>) {
    runApplication<TicketingApplication>(*args)
}
